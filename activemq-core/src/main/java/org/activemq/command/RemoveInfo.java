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
package org.activemq.command;

import java.io.IOException;

import org.activemq.state.CommandVisitor;

/**
 * Removes a consumer, producer, session or connection.
 *  
 * @openwire:marshaller
 * @version $Revision$
 */
public class RemoveInfo extends BaseCommand {
    
    public static final byte DATA_STRUCTURE_TYPE=CommandTypes.REMOVE_INFO;

    protected DataStructure objectId;

    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }    

    public RemoveInfo() {        
    }
    public RemoveInfo(DataStructure objectId) {
        this.objectId=objectId;
    }
    
    /**
     * @openwire:property version=1 cache=true
     */
    public DataStructure getObjectId() {
        return objectId;
    }

    public void setObjectId(DataStructure objectId) {
        this.objectId = objectId;
    }

    public Response visit(CommandVisitor visitor) throws Throwable {
        switch (objectId.getDataStructureType()) {
        case ConnectionId.DATA_STRUCTURE_TYPE:
            return visitor.processRemoveConnection((ConnectionId) objectId);
        case SessionId.DATA_STRUCTURE_TYPE:
            return visitor.processRemoveSession((SessionId) objectId);
        case ConsumerId.DATA_STRUCTURE_TYPE:
            return visitor.processRemoveConsumer((ConsumerId) objectId);
        case ProducerId.DATA_STRUCTURE_TYPE:
            return visitor.processRemoveProducer((ProducerId) objectId);
        default:
            throw new IOException("Unknown remove command type: "+ objectId.getDataStructureType());
        }
    }

}
