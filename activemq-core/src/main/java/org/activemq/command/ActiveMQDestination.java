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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;

import org.activemq.util.URISupport;

/**
 * @openwire:marshaller
 * @version $Revision: 1.10 $
 */
abstract public class ActiveMQDestination implements DataStructure, Destination, Externalizable {

    private static final long serialVersionUID = -3885260014960795889L;

    public static final String PATH_SEPERATOR = ".";
    public static final String COMPOSITE_SEPERATOR = ",";
    
    public static final byte QUEUE_TYPE = 0x01; 
    public static final byte TOPIC_TYPE = 0x02; 
    public static final byte TEMP_MASK  = 0x04; 
    public static final byte TEMP_TOPIC_TYPE = TOPIC_TYPE | TEMP_MASK;
    public static final byte TEMP_QUEUE_TYPE = QUEUE_TYPE | TEMP_MASK;
    
    public static final String QUEUE_QUALIFIED_PREFIX = "queue://"; 
    public static final String TOPIC_QUALIFIED_PREFIX = "topic://"; 
    public static final String TEMP_QUEUE_QUALIFED_PREFIX= "temp-queue://"; 
    public static final String TEMP_TOPIC_QUALIFED_PREFIX = "temp-topic://";
            
    protected String physicalName;
    
    transient protected ActiveMQDestination[] compositeDestinations;
    transient protected String[] destinationPaths;
    transient protected boolean isPattern;
    transient protected int hashValue;
    protected Map options;

    public ActiveMQDestination() {
    }
    
    protected ActiveMQDestination(String name) {
        setPhysicalName(name);
    }
    
    public ActiveMQDestination(ActiveMQDestination composites[]) {
        setCompositeDestinations(composites);
    }

    public boolean isComposite() {
        return compositeDestinations!=null;
    }
    public ActiveMQDestination[] getCompositeDestinations() {
        return compositeDestinations;
    }
    
    public void setCompositeDestinations(ActiveMQDestination[] destinations) {
        this.compositeDestinations=destinations;
        this.destinationPaths=null;
        this.hashValue=0;
        this.isPattern=false;
        
        StringBuffer sb = new StringBuffer(); 
        for (int i = 0; i < destinations.length; i++) {
            if( i!=0 )
                sb.append(COMPOSITE_SEPERATOR);
            if( getDestinationType()==destinations[i].getDestinationType()) {
                sb.append(destinations[i].getQualifiedName());
            } else {
                sb.append(destinations[i].getQualifiedName());
            }
        }        
        physicalName = sb.toString();
    }

    public String getQualifiedName() {
        if( isComposite() )
            return physicalName;
        return getQualifiedPrefix()+physicalName;
    }
    
    abstract protected String getQualifiedPrefix();
    
    /**
     * @openwire:property version=1
     */
    public String getPhysicalName() {
        return physicalName;
    }

    public void setPhysicalName(String physicalName) {

        // Strip off any options
        int p = physicalName.indexOf("?");
        if( p >= 0 ) {
            String optstring = physicalName.substring(p+1);
            physicalName = physicalName.substring(0, p);
            try {
                options = URISupport.parseQuery(optstring);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid destination name: "+physicalName+", it's options are not encoded properly: "+e);
            }
        }
        
        this.physicalName = physicalName;
        this.destinationPaths=null;
        this.hashValue=0;
        this.isPattern = false;
        
        // Check to see if it is a composite.
        ArrayList l = new ArrayList();
        StringTokenizer iter = new StringTokenizer(physicalName, COMPOSITE_SEPERATOR);
        while (iter.hasMoreTokens()) {
            String name = iter.nextToken().trim();
            if( name.length() == 0 )
                continue;
            l.add(name);
        }
        
        if( l.size()>1 ) {
            compositeDestinations = new ActiveMQDestination[l.size()];
            int counter=0;
            for (Iterator iterator = l.iterator(); iterator.hasNext();) {
                compositeDestinations[counter++] =createDestination((String) iterator.next());
            }
        } else {            
            compositeDestinations=null;
            // If this is a pattern destination.
            if( !isTemporary() && (
                    physicalName.indexOf("*")>=0 ||
                    physicalName.indexOf("<")>=0 ) ) {
                isPattern = true;
            }
        }
    }

    public ActiveMQDestination createDestination(String name) {
        return createDestination(name, getDestinationType());
    }
    
    static public ActiveMQDestination createDestination(String name, byte defaultType) {
        
        if( name.startsWith(QUEUE_QUALIFIED_PREFIX) ) {
            return new ActiveMQQueue(name.substring(QUEUE_QUALIFIED_PREFIX.length()));
        } else if( name.startsWith(TOPIC_QUALIFIED_PREFIX) ) {            
            return new ActiveMQTopic(name.substring(TOPIC_QUALIFIED_PREFIX.length()));
        } else if( name.startsWith(TEMP_QUEUE_QUALIFED_PREFIX) ) {            
            return new ActiveMQTempQueue(name.substring(TEMP_QUEUE_QUALIFED_PREFIX.length()));
        } else if( name.startsWith(TEMP_TOPIC_QUALIFED_PREFIX) ) {            
            return new ActiveMQTempTopic(name.substring(TEMP_TOPIC_QUALIFED_PREFIX.length()));
        }
        
        switch(defaultType) {
        case QUEUE_TYPE:
            return new ActiveMQQueue(name);
        case TOPIC_TYPE:
            return new ActiveMQTopic(name);
        case TEMP_QUEUE_TYPE:
            return new ActiveMQTempQueue(name);
        case TEMP_TOPIC_TYPE:
            return new ActiveMQTempTopic(name);
        default:
            throw new IllegalArgumentException("Invalid default destination type: "+defaultType);
        }
    }
    
    public String[] getDestinationPaths() {  
        
        if( destinationPaths!=null )
            return destinationPaths;
        
        ArrayList l = new ArrayList();
        StringTokenizer iter = new StringTokenizer(physicalName, PATH_SEPERATOR);
        while (iter.hasMoreTokens()) {
            String name = iter.nextToken().trim();
            if( name.length() == 0 )
                continue;
            l.add(name);
        }
        
        destinationPaths = new String[l.size()];
        l.toArray(destinationPaths);
        return destinationPaths;        
    }
    
    abstract public byte getDestinationType();
    
    public boolean isQueue() {
        return false;
    }
    
    public boolean isTopic() {
        return false;
    }
    
    public boolean isTemporary() {
        return false;
    }
    
    public boolean equals(Object o) {
        if( this == o )
            return true;
        if( o==null || getClass()!=o.getClass() )
            return false;
        
        ActiveMQDestination d = (ActiveMQDestination) o;
        return physicalName.equals(d.physicalName);
    }
    
    public int hashCode() {
        if( hashValue==0 ) {
            hashValue = physicalName.hashCode();
        }
        return hashValue;
    }

    public static ActiveMQDestination transform(Destination dest) throws JMSException {
        if( dest == null )
            return null;
        if( dest instanceof ActiveMQDestination )
            return (ActiveMQDestination) dest;
        if( dest instanceof TemporaryQueue )
            return new ActiveMQTempQueue(((TemporaryQueue)dest).getQueueName());
        if( dest instanceof TemporaryTopic )
            return new ActiveMQTempTopic(((TemporaryTopic)dest).getTopicName());
        if( dest instanceof Queue )
            return new ActiveMQQueue(((Queue)dest).getQueueName());
        if( dest instanceof Topic )
            return new ActiveMQTopic(((Topic)dest).getTopicName());
        throw new JMSException("Could not transform the destination into a ActiveMQ destination: "+dest);
    }
    
    public String toString() {
        return getQualifiedName();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(this.getPhysicalName());
        out.writeObject(options);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setPhysicalName(in.readUTF());
        this.options = (Map) in.readObject();
    }

    public String getDestinationTypeAsString() {
        switch(getDestinationType()) {
        case QUEUE_TYPE:
            return "Queue";
        case TOPIC_TYPE:
            return "Topic";
        case TEMP_QUEUE_TYPE:
            return "TempQueue";
        case TEMP_TOPIC_TYPE:
            return "TempTopic";
        default:
            throw new IllegalArgumentException("Invalid destination type: "+getDestinationType());
        }
    }

    public Map getOptions() {
        return options;
    }
    
    public boolean isMarshallAware() {
        return false;
    }

}
