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
package org.apache.rocketmq.broker.eventtrack.listener;

import org.apache.rocketmq.broker.eventtrack.EventTrackerManager;
import org.apache.rocketmq.broker.eventtrack.EventType;
import org.apache.rocketmq.broker.eventtrack.event.TopicEventDetail;
import org.apache.rocketmq.broker.topic.TopicChangeListener;
import org.apache.rocketmq.broker.topic.TopicEvent;
import org.apache.rocketmq.common.TopicConfig;

public class TopicEventTrackListener implements TopicChangeListener {
    @Override
    public void handle(TopicEvent event, TopicConfig topicConfig) {
        if (event == null) {
            return;
        }

        switch (event) {
            case TOPIC_CREATE:
            case TOPIC_UPDATE:
            case TOPIC_DELETE:
                TopicEventDetail topicEvent = new TopicEventDetail(topicConfig);
                topicEvent.setSubType(event);
                EventTrackerManager.trackEvent(EventType.TOPIC_EVENT, topicEvent);
                break;
            default:
                break;
        }
    }
}
