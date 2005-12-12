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
package org.activemq.filter;

import javax.jms.JMSException;

/**
 * A filter performing a comparison of two objects
 * 
 * @version $Revision: 1.2 $
 */
public abstract class LogicExpression extends BinaryExpression implements BooleanExpression {

    public static BooleanExpression createOR(BooleanExpression lvalue, BooleanExpression rvalue) {
        return new LogicExpression(lvalue, rvalue) {
        	
            public Object evaluate(MessageEvaluationContext message) throws JMSException {
                
            	Boolean lv = (Boolean) left.evaluate(message);
                // Can we do an OR shortcut??
            	if (lv !=null && lv.booleanValue()) {
                    return Boolean.TRUE;
                }
            	
                Boolean rv = (Boolean) right.evaluate(message);
                return rv==null ? null : rv;
            }

            public String getExpressionSymbol() {
                return "OR";
            }
        };
    }

    public static BooleanExpression createAND(BooleanExpression lvalue, BooleanExpression rvalue) {
        return new LogicExpression(lvalue, rvalue) {

            public Object evaluate(MessageEvaluationContext message) throws JMSException {

                Boolean lv = (Boolean) left.evaluate(message);

                // Can we do an AND shortcut??
                if (lv == null)
                    return null;
                if (!lv.booleanValue()) {
                    return Boolean.FALSE;
                }

                Boolean rv = (Boolean) right.evaluate(message);
                return rv == null ? null : rv;
            }

            public String getExpressionSymbol() {
                return "AND";
            }
        };
    }

    /**
     * @param left
     * @param right
     */
    public LogicExpression(BooleanExpression left, BooleanExpression right) {
        super(left, right);
    }

    abstract public Object evaluate(MessageEvaluationContext message) throws JMSException;

    public boolean matches(MessageEvaluationContext message) throws JMSException {
        Object object = evaluate(message);
        return object!=null && object==Boolean.TRUE;            
    }

}
