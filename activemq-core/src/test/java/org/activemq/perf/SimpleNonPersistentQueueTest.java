/**
 * <a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>
 * 
 * Copyright 2005 (C) Simula Labs Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 */
package org.activemq.perf;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
/**
 * @version $Revision: 1.3 $
 */
public class SimpleNonPersistentQueueTest extends SimpleQueueTest{
    
    protected PerfProducer createProducer(ConnectionFactory fac,Destination dest,int number) throws JMSException{
        PerfProducer pp=new PerfProducer(fac,dest);
        pp.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        return pp;
    }
}