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
package org.activemq;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Defines the pretech message policies for different types of consumers
 * @version $Revision: 1.3 $
 */
public class ActiveMQPrefetchPolicy {
    private static final Log log = LogFactory.getLog(ActiveMQPrefetchPolicy.class);
    private static final int MAX_PREFETCH_SIZE = (Short.MAX_VALUE -1);
    private int queuePrefetch;
    private int queueBrowserPrefetch;
    private int topicPrefetch;
    private int durableTopicPrefetch;
    private int inputStreamPrefetch;


    /**
     * Initialize default prefetch policies
     */
    public ActiveMQPrefetchPolicy() {
        this.queuePrefetch = 1000;
        this.queueBrowserPrefetch = 500;
        this.topicPrefetch = MAX_PREFETCH_SIZE;
        this.durableTopicPrefetch = 100;
        this.inputStreamPrefetch = 100;
    }

    /**
     * @return Returns the durableTopicPrefetch.
     */
    public int getDurableTopicPrefetch() {
        return durableTopicPrefetch;
    }

    /**
     * @param durableTopicPrefetch The durableTopicPrefetch to set.
     */
    public void setDurableTopicPrefetch(int durableTopicPrefetch) {
        this.durableTopicPrefetch = getMaxPrefetchLimit(durableTopicPrefetch);
    }

    /**
     * @return Returns the queuePrefetch.
     */
    public int getQueuePrefetch() {
        return queuePrefetch;
    }

    /**
     * @param queuePrefetch The queuePrefetch to set.
     */
    public void setQueuePrefetch(int queuePrefetch) {
        this.queuePrefetch = getMaxPrefetchLimit(queuePrefetch);
    }

    /**
     * @return Returns the queueBrowserPrefetch.
     */
    public int getQueueBrowserPrefetch() {
        return queueBrowserPrefetch;
    }

    /**
     * @param queueBrowserPrefetch The queueBrowserPrefetch to set.
     */
    public void setQueueBrowserPrefetch(int queueBrowserPrefetch) {
        this.queueBrowserPrefetch = getMaxPrefetchLimit(queueBrowserPrefetch);
    }

    /**
     * @return Returns the topicPrefetch.
     */
    public int getTopicPrefetch() {
        return topicPrefetch;
    }

    /**
     * @param topicPrefetch The topicPrefetch to set.
     */
    public void setTopicPrefetch(int topicPrefetch) {
        this.topicPrefetch = getMaxPrefetchLimit(topicPrefetch);
    }
    
    private int getMaxPrefetchLimit(int value) {
        int result = Math.min(value, MAX_PREFETCH_SIZE);
        if (result < value) {
            log.warn("maximum prefetch limit has been reset from " + value + " to " + MAX_PREFETCH_SIZE);
        }
        return result;
    }

    public void setAll(int i) {
        this.durableTopicPrefetch=i;
        this.queueBrowserPrefetch=i;
        this.queuePrefetch=i;
        this.topicPrefetch=i;
        this.inputStreamPrefetch=1;
    }

    public int getInputStreamPrefetch() {
        return inputStreamPrefetch;
    }

    public void setInputStreamPrefetch(int inputStreamPrefetch) {
        this.inputStreamPrefetch = getMaxPrefetchLimit(inputStreamPrefetch);
    }
}
