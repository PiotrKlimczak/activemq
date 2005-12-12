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
package org.activemq.util;

import java.util.Map;

/**
 * A bunch of utility methods for working with maps
 *
 * @version $Revision$
 */
public class MapHelper {
    /**
     * Extracts the value from the map and coerces to a String
     */
    public static String getString(Map map, String key) {
        Object answer = map.get(key);
        return (answer != null) ? answer.toString() : null;
    }

    /**
     * Extracts the value from the map and coerces to an int value
     * or returns a default value if one could not be found or coerced
     */
    public static int getInt(Map map, String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        else if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        return defaultValue;
    }
}
