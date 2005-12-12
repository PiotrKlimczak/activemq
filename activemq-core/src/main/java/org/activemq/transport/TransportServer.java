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
package org.activemq.transport;

import java.net.URI;

import org.activemq.Service;
import org.activemq.command.BrokerInfo;


/**
 * A TransportServer asynchronously accepts {@see Transport} objects
 * and then delivers those objects to a {@see TransportAcceptListener}.
 * 
 * @version $Revision: 1.4 $
 */
public interface TransportServer extends Service {
	
	/**
	 * Registers an {@see TransportAcceptListener} which is notified of accepted channels.
	 *  
	 * @param acceptListener
	 */
    void setAcceptListener(TransportAcceptListener acceptListener);
    
    /**
     * Associates a broker info with the transport server so that the transport can do
     * discovery advertisements of the broker.
     * 
     * @param brokerInfo
     */
    void setBrokerInfo(BrokerInfo brokerInfo);

    public URI getConnectURI();
    
}
