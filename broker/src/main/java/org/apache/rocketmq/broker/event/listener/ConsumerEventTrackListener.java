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
package org.apache.rocketmq.broker.event.listener;

import java.util.Set;
import org.apache.rocketmq.broker.client.ClientChannelInfo;
import org.apache.rocketmq.broker.client.ClientOfflineType;
import org.apache.rocketmq.broker.client.ConsumerGroupEvent;
import org.apache.rocketmq.broker.client.ConsumerIdsChangeListener;
import org.apache.rocketmq.common.event.EventTrackerManager;
import org.apache.rocketmq.common.event.EventType;
import org.apache.rocketmq.broker.event.detail.ConsumerClientEventDetail;
import org.apache.rocketmq.broker.event.detail.ConsumerGroupEventDetail;

import static org.apache.rocketmq.broker.client.ConsumerGroupEvent.CLIENT_REGISTER;
import static org.apache.rocketmq.broker.client.ConsumerGroupEvent.CLIENT_UNREGISTER;

public class ConsumerEventTrackListener implements ConsumerIdsChangeListener {
    @Override
    public void handle(ConsumerGroupEvent event, String group, Object... args) {
        if (event == null) {
            return;
        }
        switch (event) {
            case UNREGISTER:
                ConsumerGroupEventDetail consumerGroupEventDetail = new ConsumerGroupEventDetail(group, ConsumerGroupEventDetail.EventType.CONSUMER_GROUP_UNREGISTER_EVENT);
                EventTrackerManager.trackEvent(EventType.CONSUMER_GROUP_EVENT, consumerGroupEventDetail);
                break;
            case GROUP_REGISTER:
                ConsumerGroupEventDetail consumerGroupRegisterEvent = new ConsumerGroupEventDetail(group, ConsumerGroupEventDetail.EventType.CONSUMER_GROUP_REGISTER_EVENT);
                EventTrackerManager.trackEvent(EventType.CONSUMER_GROUP_EVENT, consumerGroupRegisterEvent);
                break;
            case CLIENT_REGISTER:
                if (args == null || args.length < 2) {
                    return;
                }
                ConsumerClientEventDetail consumerClientRegisterEvent = new ConsumerClientEventDetail(group, CLIENT_REGISTER,
                    (ClientChannelInfo) args[0], (Set<String>) args[1]);
                EventTrackerManager.trackEvent(EventType.CONSUMER_GROUP_CLIENT_EVENT, consumerClientRegisterEvent);
            case CLIENT_UNREGISTER:
                if (args == null || args.length < 3) {
                    return;
                }
                ConsumerClientEventDetail consumerClientUnregisterEvent = new ConsumerClientEventDetail(group, CLIENT_UNREGISTER,
                    (ClientChannelInfo) args[0], (Set<String>) args[1], (ClientOfflineType) args[2]);
                EventTrackerManager.trackEvent(EventType.CONSUMER_GROUP_CLIENT_EVENT, consumerClientUnregisterEvent);
                break;
        }
    }

    @Override
    public void shutdown() {

    }
}
