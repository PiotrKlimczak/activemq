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
package org.activemq.broker.region.policy;

import java.util.Iterator;

import org.activemq.broker.ConnectionContext;
import org.activemq.broker.region.MessageReference;
import org.activemq.broker.region.Subscription;
import org.activemq.filter.MessageEvaluationContext;

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;

/**
 * Simple dispatch policy that sends a message to every subscription that 
 * matches the message.
 * 
 * @org.xbean.XBean
 * 
 * @version $Revision$
 */
public class SimpleDispatchPolicy implements DispatchPolicy {

    public void dispatch(ConnectionContext newParam, MessageReference node, MessageEvaluationContext msgContext, CopyOnWriteArrayList consumers) throws Throwable {
        
        for (Iterator iter = consumers.iterator(); iter.hasNext();) {
            Subscription sub = (Subscription) iter.next();
            
            // Don't deliver to browsers
            if( sub.getConsumerInfo().isBrowser() )
                continue;
            // Only dispatch to interested subscriptions
            if (!sub.matches(node, msgContext)) 
                continue;
            
            sub.add(node);
        }
    }

}
