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

package org.activemq.spring;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;
import java.util.List;

public class ConsumerBean implements MessageListener {
    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
            .getLog(ConsumerBean.class);
    
    private List messages = new ArrayList();
    private Object semaphore;

    /**
     * Constructor.
     */
    public ConsumerBean() {
        this(new Object());
    }

    /**
     * Constructor, initialized semaphore object.
     * @param semaphore
     */
    public ConsumerBean(Object semaphore) {
        this.semaphore = semaphore;
    }

    /**
     * @return all the messages on the list so far, clearing the buffer
     */
    public synchronized List flushMessages() {
        List answer = new ArrayList(messages);
        messages.clear();
        return answer;
    }

    /**
     * Method implemented from MessageListener interface.
     * @param message
     */
    public synchronized void onMessage(Message message) {
        messages.add(message);
        synchronized (semaphore) {
            semaphore.notifyAll();
        }
    }

    /**
     * Use to wait for a single message to arrive.
     */
    public void waitForMessageToArrive() {
        log.info("Waiting for message to arrive");

        long start = System.currentTimeMillis();

        try {
            if (hasReceivedMessage()) {
                synchronized (semaphore) {
                    semaphore.wait(4000);
                }
            }
        }
        catch (InterruptedException e) {
            log.info("Caught: " + e);
        }
        long end = System.currentTimeMillis() - start;

        log.info("End of wait for " + end + " millis");
    }

    /**
     * Used to wait for a message to arrive given a particular message count.
     * @param messageCount
     */
    public void waitForMessagesToArrive(int messageCount) {
        log.info("Waiting for message to arrive");

        long start = System.currentTimeMillis();

        for (int i = 0; i < 10; i++) {
            try {
                if (hasReceivedMessages(messageCount)) {
                    break;
                }
                synchronized (semaphore) {
                    semaphore.wait(1000);
                }
            }
            catch (InterruptedException e) {
                log.info("Caught: " + e);
            }
        }
        long end = System.currentTimeMillis() - start;

        log.info("End of wait for " + end + " millis");
    }

    /**
     * Identifies if the message is empty.
     * @return
     */
    protected boolean hasReceivedMessage() {
        return messages.isEmpty();
    }

    /**
     * Identifies if the message count has reached the total size of message.
     * @param messageCount
     * @return
     */
    protected synchronized boolean hasReceivedMessages(int messageCount) {
        return messages.size() >= messageCount;
    }
}
