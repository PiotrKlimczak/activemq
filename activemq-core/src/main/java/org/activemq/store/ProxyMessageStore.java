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
package org.activemq.store;

import java.io.IOException;

import org.activemq.broker.ConnectionContext;
import org.activemq.command.ActiveMQDestination;
import org.activemq.command.Message;
import org.activemq.command.MessageAck;
import org.activemq.command.MessageId;

/**
 * A simple proxy that delegates to another MessageStore.
 */
public class ProxyMessageStore implements MessageStore {

    final MessageStore delegate;
    
    public ProxyMessageStore(MessageStore delegate) {
        this.delegate = delegate;
    }
    
    public MessageStore getDelegate() {
        return delegate;
    }

    public void addMessage(ConnectionContext context, Message message) throws IOException {
        delegate.addMessage(context, message);
    }
    public Message getMessage(MessageId identity) throws IOException {
        return delegate.getMessage(identity);
    }
    public void recover(MessageRecoveryListener listener) throws Throwable {
        delegate.recover(listener);
    }
    public void removeAllMessages(ConnectionContext context) throws IOException {
        delegate.removeAllMessages(context);
    }
    public void removeMessage(ConnectionContext context, MessageAck ack) throws IOException {
        delegate.removeMessage(context, ack);
    }
    public void start() throws IOException {
        delegate.start();
    }    
    public void stop(long timeout) throws IOException {
        delegate.stop(timeout);
    }
    public ActiveMQDestination getDestination() {
        return delegate.getDestination();
    }

    public void addMessageReference(ConnectionContext context, MessageId messageId, long expirationTime, String messageRef) throws IOException {
        delegate.addMessageReference(context, messageId, expirationTime, messageRef);
    }

    public String getMessageReference(MessageId identity) throws IOException {
        return delegate.getMessageReference(identity);
    }
}
