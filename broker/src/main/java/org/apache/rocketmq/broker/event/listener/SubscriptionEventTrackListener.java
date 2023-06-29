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

import org.apache.rocketmq.broker.client.SubscriptionChangeListener;
import org.apache.rocketmq.broker.client.SubscriptionEvent;
import org.apache.rocketmq.common.event.EventTrackerManager;
import org.apache.rocketmq.common.event.EventType;
import org.apache.rocketmq.broker.event.detail.SubscriptionChangeEventDetail;
import org.apache.rocketmq.remoting.protocol.heartbeat.SubscriptionData;

public class SubscriptionEventTrackListener implements SubscriptionChangeListener {
    @Override
    public void handle(String groupName, SubscriptionEvent type, SubscriptionData originSubscription, SubscriptionData newSubscription) {
        SubscriptionChangeEventDetail subscriptionChangeEventDetail = new SubscriptionChangeEventDetail(groupName,
            type, originSubscription, newSubscription);
        EventTrackerManager.trackEvent(EventType.SUBSCRIPTION_EVENT, subscriptionChangeEventDetail);
    }
}
