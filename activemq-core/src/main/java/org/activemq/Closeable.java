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

import javax.jms.JMSException;

/**
 * Provides a uniform interface that can be used to close all the JMS obejcts
 * that provide a close() method.  Useful for when you want to collect
 * a heterogeous set of JMS object in a collection to be closed at a later time.
 *
 * @version $Revision: 1.2 $
 */
public interface Closeable {
	
	/**
	 * Closes a JMS object.
	 * <P>
	 * Many JMS objects are closeable such as Connections, Sessions, Consumers and Producers.
	 *
	 * @throws JMSException if the JMS provider fails to close the object due to
	 *                      some internal error.
	 */
	public void close() throws JMSException;
}