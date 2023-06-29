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
package org.apache.rocketmq.broker.event.detail;

import org.apache.rocketmq.common.event.EventDetail;
import org.apache.rocketmq.remoting.protocol.subscription.GroupRetryPolicy;
import org.apache.rocketmq.remoting.protocol.subscription.RetryPolicy;
import org.apache.rocketmq.remoting.protocol.subscription.SubscriptionGroupConfig;

public class ConsumerGroupEventDetail implements EventDetail {

    private String groupName;
    private Boolean consumeMessageOrderly;
    private GroupRetryPolicy groupRetryPolicy;
    private RetryPolicy retryPolicy;
    private Integer retryMaxTimes;
    private EventType subType;

    public enum EventType {
        CONSUMER_GROUP_CREATE_EVENT,
        CONSUMER_GROUP_DELETE_EVENT,
        CONSUMER_GROUP_UPDATE_EVENT,

        CONSUMER_GROUP_REGISTER_EVENT,
        CONSUMER_GROUP_UNREGISTER_EVENT,
    }

    public ConsumerGroupEventDetail(SubscriptionGroupConfig groupConfig,
        ConsumerGroupEventDetail.EventType subType) {
        this.groupName = groupConfig.getGroupName();
        this.consumeMessageOrderly = groupConfig.isConsumeMessageOrderly();
        this.groupRetryPolicy = groupConfig.getGroupRetryPolicy();
        this.retryPolicy = groupConfig.getGroupRetryPolicy().getRetryPolicy();
        this.retryMaxTimes = groupConfig.getRetryMaxTimes();
        this.subType = subType;
    }

    public ConsumerGroupEventDetail(String groupName, ConsumerGroupEventDetail.EventType subType) {
        this.groupName = groupName;
        this.subType = subType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Boolean getConsumeMessageOrderly() {
        return consumeMessageOrderly;
    }

    public void setConsumeMessageOrderly(Boolean consumeMessageOrderly) {
        this.consumeMessageOrderly = consumeMessageOrderly;
    }

    public GroupRetryPolicy getGroupRetryPolicy() {
        return groupRetryPolicy;
    }

    public void setGroupRetryPolicy(GroupRetryPolicy groupRetryPolicy) {
        this.groupRetryPolicy = groupRetryPolicy;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public Integer getRetryMaxTimes() {
        return retryMaxTimes;
    }

    public void setRetryMaxTimes(Integer retryMaxTimes) {
        this.retryMaxTimes = retryMaxTimes;
    }

    public EventType getSubType() {
        return subType;
    }

    public void setSubType(EventType subType) {
        this.subType = subType;
    }
}
