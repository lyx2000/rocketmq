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

import java.util.Map;
import org.apache.rocketmq.common.event.EventDetail;
import org.apache.rocketmq.broker.topic.TopicEvent;
import org.apache.rocketmq.common.TopicConfig;
import org.apache.rocketmq.common.attribute.TopicMessageType;

public class TopicEventDetail implements EventDetail {

    private String topicName;
    private TopicMessageType topicMessageType;
    private Map<String, String> attributes;
    public TopicEvent subType;

    public TopicEventDetail(TopicConfig topicConfig) {
        this.topicName = topicConfig.getTopicName();
        this.topicMessageType = topicConfig.getTopicMessageType();
        this.attributes = topicConfig.getAttributes();
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public TopicMessageType getTopicMessageType() {
        return topicMessageType;
    }

    public void setTopicMessageType(TopicMessageType topicMessageType) {
        this.topicMessageType = topicMessageType;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public TopicEvent getSubType() {
        return subType;
    }

    public void setSubType(TopicEvent subType) {
        this.subType = subType;
    }
}


