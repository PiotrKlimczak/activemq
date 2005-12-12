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

/**
 * 
 * @openwire:marshaller
 * @version $Revision$
 */
public class SessionId implements DataStructure  {

    public static final byte DATA_STRUCTURE_TYPE=CommandTypes.SESSION_ID;

    protected String connectionId;
    protected long sessionId;
    
    protected transient int hashCode;
    protected transient String key;
    protected transient ConnectionId parentId;
    
    public SessionId() {        
    }
        
    public SessionId(ConnectionId connectionId, long sessionId) {
        this.connectionId = connectionId.getConnectionId();
        this.sessionId=sessionId;
    }
    
    public SessionId(SessionId id) {
        this.connectionId = id.getConnectionId();
        this.sessionId=id.getSessionId();
    }

    public SessionId(ProducerId id) {
        this.connectionId = id.getConnectionId();
        this.sessionId=id.getSessionId();
    }
    
    public SessionId(ConsumerId id) {
        this.connectionId = id.getConnectionId();
        this.sessionId=id.getSessionId();
    }
    
    public ConnectionId getParentId() {
        if( parentId == null ) {
            parentId = new ConnectionId(this);
        }
        return parentId;
    }

    public int hashCode() {
        if( hashCode == 0 ) {
            hashCode = connectionId.hashCode() ^ (int)sessionId;
        }
        return hashCode;
    }
    
    public boolean equals(Object o) {
        if( this == o )
            return true;
        if( o == null || o.getClass()!=SessionId.class )
            return false;
        SessionId id = (SessionId) o;
        return sessionId==id.sessionId 
               && connectionId.equals(id.connectionId);
    }

    public byte getDataStructureType() {
        return DATA_STRUCTURE_TYPE;
    }

    /**
     * @openwire:property version=1 cache=true
     */
    public String getConnectionId() {
        return connectionId;
    }    
    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    /**
     * @openwire:property version=1
     */
    public long getSessionId() {
        return sessionId;
    }
    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }
    
    public String toString() {
        if( key==null ) {
            key = connectionId+":"+sessionId;
        }
        return key;
    }

    public boolean isMarshallAware() {
        return false;
    }
}
