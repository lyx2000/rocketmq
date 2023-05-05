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
package org.apache.rocketmq.broker.eventtrack;

import java.util.List;
import org.apache.rocketmq.broker.eventtrack.event.EventDetail;
import org.apache.rocketmq.common.constant.LoggerName;
import org.apache.rocketmq.common.utils.ServiceProvider;
import org.apache.rocketmq.logging.org.slf4j.Logger;
import org.apache.rocketmq.logging.org.slf4j.LoggerFactory;

public class EventTrackerManager {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.COMMON_LOGGER_NAME);

    private final static EventContext EVENT_CONTEXT = new EventContext();

    public static void initEventContext(String nodeId, String nodeType) {
        EVENT_CONTEXT.setNodeId(nodeId);
        EVENT_CONTEXT.setNodeType(nodeType);
    }

    static List<EventTracker> eventTrackers;

    static {
        eventTrackers = ServiceProvider.load(EventTracker.class);
        if (eventTrackers.isEmpty()) {
            log.info("ServiceProvider loaded no EventTracker, using default org.apache.rocketmq.broker.eventtrack.LogEventTracker");
            eventTrackers.add(new LogEventTracker());
        }

    }

    public static void trackEvent(EventType type, EventDetail detail) {
        EventContext eventContext = getEventContext();
        eventContext.setDetail(detail);
        eventContext.setType(type);

        for (EventTracker eventTracker : eventTrackers) {
            eventTracker.trackEvent(eventContext);
        }
    }

    public static void trackEvent(EventType type) {
        trackEvent(type, null);
    }

    private static EventContext getEventContext() {
        EventContext eventContext = new EventContext();

        eventContext.setTimeStamp(System.currentTimeMillis());
        eventContext.setNodeType(EVENT_CONTEXT.getNodeType());
        eventContext.setNodeId(EVENT_CONTEXT.getNodeId());

        return eventContext;
    }

}
