/**
 * 
 * Copyright 2005 LogicBlaze, Inc. http://www.logicblaze.com
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
package org.activemq.memory.buffer;

import org.activemq.command.ActiveMQMessage;

/**
 * A message implementation which is useful for testing as we can spoof its size
 *  
 * @version $Revision: 1.1 $
 */
public class DummyMessage extends ActiveMQMessage {

    private int size;

    public DummyMessage(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public String toString() {
        return "DummyMessage[id=" + getMessageId() + " size=" + size + "]"; 
    }
    
    
}
