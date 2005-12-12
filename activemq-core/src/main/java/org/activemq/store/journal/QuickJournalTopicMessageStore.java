/**
* <a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>
*
* Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
**/
package org.activemq.store.journal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.activeio.journal.RecordLocation;
import org.activemq.broker.ConnectionContext;
import org.activemq.command.ActiveMQTopic;
import org.activemq.command.JournalTopicAck;
import org.activemq.command.Message;
import org.activemq.command.MessageId;
import org.activemq.command.SubscriptionInfo;
import org.activemq.store.MessageRecoveryListener;
import org.activemq.store.TopicMessageStore;
import org.activemq.transaction.Synchronization;
import org.activemq.util.Callback;
import org.activemq.util.SubscriptionKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A MessageStore that uses a Journal to store it's messages.
 * 
 * @version $Revision: 1.13 $
 */
public class QuickJournalTopicMessageStore extends QuickJournalMessageStore implements TopicMessageStore {
    
    private static final Log log = LogFactory.getLog(QuickJournalTopicMessageStore.class);

    private TopicMessageStore longTermStore;
	private HashMap ackedLastAckLocations = new HashMap();
    
    public QuickJournalTopicMessageStore(QuickJournalPersistenceAdapter adapter, TopicMessageStore checkpointStore, ActiveMQTopic destinationName) {
        super(adapter, checkpointStore, destinationName);
        this.longTermStore = checkpointStore;
    }
    
    public void recoverSubscription(String clientId, String subscriptionName, final MessageRecoveryListener listener) throws Throwable {
        this.peristenceAdapter.checkpoint(true, true);
        longTermStore.recoverSubscription(clientId, subscriptionName, new MessageRecoveryListener() {
            public void recoverMessage(Message message) throws Throwable {
                throw new IOException("Should not get called.");
            }
            public void recoverMessageReference(String messageReference) throws Throwable {
                RecordLocation loc = toRecordLocation(messageReference);
                Message message = (Message) peristenceAdapter.readCommand(loc);
                listener.recoverMessage(message);
            }
        });

    }

    public SubscriptionInfo lookupSubscription(String clientId, String subscriptionName) throws IOException {
        return longTermStore.lookupSubscription(clientId, subscriptionName);
    }

    public void addSubsciption(String clientId, String subscriptionName, String selector, boolean retroactive) throws IOException {
        this.peristenceAdapter.checkpoint(true, true);
        longTermStore.addSubsciption(clientId, subscriptionName, selector, retroactive);
    }

    public void addMessage(ConnectionContext context, Message message) throws IOException {
        super.addMessage(context, message);
        this.peristenceAdapter.checkpoint(false, false);
    }
    
    /**
     */
    public void acknowledge(ConnectionContext context, String clientId, String subscriptionName, final MessageId messageId) throws IOException {
        final boolean debug = log.isDebugEnabled();
        
        JournalTopicAck ack = new JournalTopicAck();
        ack.setDestination(destination);
        ack.setMessageId(messageId);
        ack.setMessageSequenceId(messageId.getBrokerSequenceId());
        ack.setSubscritionName(subscriptionName);
        ack.setClientId(clientId);
        ack.setTransactionId( context.getTransaction()!=null ? context.getTransaction().getTransactionId():null);
        final RecordLocation location = peristenceAdapter.writeCommand(ack, false);
        
        final SubscriptionKey key = new SubscriptionKey(clientId, subscriptionName);        
        if( !context.isInTransaction() ) {
            if( debug )
                log.debug("Journalled acknowledge for: "+messageId+", at: "+location);
            acknowledge(messageId, location, key);
        } else {
            if( debug )
                log.debug("Journalled transacted acknowledge for: "+messageId+", at: "+location);
            synchronized (this) {
                inFlightTxLocations.add(location);
            }
            transactionStore.acknowledge(this, ack, location);
            context.getTransaction().addSynchronization(new Synchronization(){
                public void afterCommit() {                    
                    if( debug )
                        log.debug("Transacted acknowledge commit for: "+messageId+", at: "+location);
                    synchronized (QuickJournalTopicMessageStore.this) {
                        inFlightTxLocations.remove(location);
                        acknowledge(messageId, location, key);
                    }
                }
                public void afterRollback() {                    
                    if( debug )
                        log.debug("Transacted acknowledge rollback for: "+messageId+", at: "+location);
                    synchronized (QuickJournalTopicMessageStore.this) {
                        inFlightTxLocations.remove(location);
                    }
                }
            });
        }
        
    }
    
    public void replayAcknowledge(ConnectionContext context, String clientId, String subscritionName, MessageId messageId) {
        try {
            SubscriptionInfo sub = longTermStore.lookupSubscription(clientId, subscritionName);
            if( sub != null ) {
                longTermStore.acknowledge(context, clientId, subscritionName, messageId);
            }
        }
        catch (Throwable e) {
            log.debug("Could not replay acknowledge for message '" + messageId + "'.  Message may have already been acknowledged. reason: " + e);
        }
    }
        

    /**
     * @param messageId
     * @param location
     * @param key
     */
    private void acknowledge(MessageId messageId, RecordLocation location, SubscriptionKey key) {
        synchronized(this) {
		    lastLocation = location;
		    ackedLastAckLocations.put(key, messageId);
		}
    }
    
    public RecordLocation checkpoint() throws IOException {
        
		final HashMap cpAckedLastAckLocations;

        // swap out the hash maps..
        synchronized (this) {
            cpAckedLastAckLocations = this.ackedLastAckLocations;
            this.ackedLastAckLocations = new HashMap();
        }

        return super.checkpoint( new Callback() {
            public void execute() throws Throwable {

                // Checkpoint the acknowledged messages.
                Iterator iterator = cpAckedLastAckLocations.keySet().iterator();
                while (iterator.hasNext()) {
                    SubscriptionKey subscriptionKey = (SubscriptionKey) iterator.next();
                    MessageId identity = (MessageId) cpAckedLastAckLocations.get(subscriptionKey);
                    longTermStore.acknowledge(transactionTemplate.getContext(), subscriptionKey.clientId, subscriptionKey.subscriptionName, identity);
                }

            }
        });

    }

    /**
	 * @return Returns the longTermStore.
	 */
	public TopicMessageStore getLongTermTopicMessageStore() {
		return longTermStore;
	}

    public void deleteSubscription(String clientId, String subscriptionName) throws IOException {
        longTermStore.deleteSubscription(clientId, subscriptionName);
    }

}