/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.common.event;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.constant.LoggerName;
import org.apache.rocketmq.logging.org.slf4j.Logger;
import org.apache.rocketmq.logging.org.slf4j.LoggerFactory;

public class EventTrackerUtil {

    private static final Logger log = LoggerFactory.getLogger(LoggerName.COMMON_LOGGER_NAME);

    public static Set<EventType> decodeEventList(String enableTrackers) {
        Set<EventType> eventTypeSet = Sets.newHashSet();
        if (StringUtils.isNotBlank(enableTrackers)) {
            List<String> trackerNames = Splitter.on(',').omitEmptyStrings().splitToList(enableTrackers);
            for (String track: trackerNames) {
                try {
                    eventTypeSet.add(EventType.valueOf(track));
                } catch (IllegalArgumentException illegalArgumentException) {
                    log.warn("The enableTrackers parameter is incorrectly configured, eventType {} Not available", track);
                }
            }
        }
        return eventTypeSet;
    }

    public static boolean checkEvent(EventType eventType, Set<EventType> eventTypeSet) {
        if (eventTypeSet == null) {
            return false;
        }
        return eventTypeSet.isEmpty() || eventTypeSet.contains(eventType);
    }

}
