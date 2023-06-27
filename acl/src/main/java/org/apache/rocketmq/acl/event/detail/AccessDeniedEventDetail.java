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
package org.apache.rocketmq.acl.event.detail;

import org.apache.rocketmq.acl.common.Permission;
import org.apache.rocketmq.acl.plain.PlainAccessResource;
import org.apache.rocketmq.common.event.EventDetail;

public class AccessDeniedEventDetail implements EventDetail {

    private String clientId;
    private String remoteAddress;
    private String ak;
    private Integer requestCode;

    private String consumerGroup;
    private String topic;
    private String permission;
    private EventType subType;

    public AccessDeniedEventDetail(AccessDeniedEventDetail.EventType subType, String clientId, String ak, String remoteAddress) {
        this.subType = subType;
        this.clientId = clientId;
        this.ak = ak;
        this.remoteAddress = remoteAddress;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public Integer getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(Integer requestCode) {
        this.requestCode = requestCode;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public EventType getSubType() {
        return subType;
    }

    public void setSubType(EventType subType) {
        this.subType = subType;
    }

    public void setResourceAction(String resource, Byte permission, boolean isGroup) {
        this.permission = Permission.parsePermFromByte(permission);
        if (isGroup) {
            this.consumerGroup = PlainAccessResource.getGroupFromRetryTopic(resource);
        } else {
            this.topic = resource;
        }

    }

    public enum EventType {
        WRONG_ACL_CONFIG,

        NO_PERMISSION,

        NO_ADMIN_PERMISSION,
    }
}
