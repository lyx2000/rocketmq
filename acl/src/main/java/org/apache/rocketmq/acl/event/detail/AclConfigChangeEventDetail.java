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

import org.apache.rocketmq.common.PlainAccessConfig;
import org.apache.rocketmq.common.event.EventDetail;

public class AclConfigChangeEventDetail implements EventDetail {
    private PlainAccessConfig newConfig;
    private PlainAccessConfig originConfig;
    private EventType subType;

    public enum EventType {
        ACL_ACCOUNT_CREATE_EVENT,
        ACL_ACCOUNT_DELETE_EVENT,
        ACL_ACCOUNT_UPDATE_EVENT,
    }

    public AclConfigChangeEventDetail(PlainAccessConfig newConfig, PlainAccessConfig originConfig, EventType subType) {
        this.newConfig = newConfig;
        this.originConfig = originConfig;
        this.subType = subType;
    }

    public PlainAccessConfig getNewConfig() {
        return newConfig;
    }

    public void setNewConfig(PlainAccessConfig newConfig) {
        this.newConfig = newConfig;
    }

    public PlainAccessConfig getOriginConfig() {
        return originConfig;
    }

    public void setOriginConfig(PlainAccessConfig originConfig) {
        this.originConfig = originConfig;
    }

    public EventType getSubType() {
        return subType;
    }

    public void setSubType(EventType subType) {
        this.subType = subType;
    }

}
