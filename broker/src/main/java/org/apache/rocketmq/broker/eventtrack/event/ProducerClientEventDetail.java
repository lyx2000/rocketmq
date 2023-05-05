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
package org.apache.rocketmq.broker.eventtrack.event;

import org.apache.rocketmq.broker.client.ClientChannelInfo;
import org.apache.rocketmq.broker.client.ClientOfflineType;
import org.apache.rocketmq.broker.client.ProducerGroupEvent;

public class ProducerClientEventDetail implements EventDetail {

    private String groupName;
    private String clientId;
    private String language;
    private int version;
    private long lastUpdateTimestamp;
    private ClientOfflineType clientOfflineType;
    private ProducerGroupEvent subType;

    public ProducerClientEventDetail(String group, ClientChannelInfo clientChannelInfo, ClientOfflineType offlineType) {
        this.groupName = group;
        this.clientId = clientChannelInfo.getClientId();
        this.language = clientChannelInfo.getLanguage().name();
        this.version = clientChannelInfo.getVersion();
        this.lastUpdateTimestamp = clientChannelInfo.getLastUpdateTimestamp();
        this.clientOfflineType = offlineType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public ClientOfflineType getClientOfflineType() {
        return clientOfflineType;
    }

    public void setClientOfflineType(ClientOfflineType clientOfflineType) {
        this.clientOfflineType = clientOfflineType;
    }

    public ProducerGroupEvent getSubType() {
        return subType;
    }

    public void setSubType(ProducerGroupEvent subType) {
        this.subType = subType;
    }
}
