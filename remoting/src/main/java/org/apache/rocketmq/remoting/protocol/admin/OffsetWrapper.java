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
package org.apache.rocketmq.remoting.protocol.admin;

public class OffsetWrapper {
    private long brokerOffset;
    private long consumerOffset;
    private long pullOffset;


    private long lagEstimatedAccumulation;
    private long inFlightMsgCountEstimatedAccumulation;

    private long lastTimestamp;
    private long earliestUnconsumedTimestamp;
    private long earliestUnPulledTimestamp;

    public long getBrokerOffset() {
        return brokerOffset;
    }

    public void setBrokerOffset(long brokerOffset) {
        this.brokerOffset = brokerOffset;
    }

    public long getConsumerOffset() {
        return consumerOffset;
    }

    public void setConsumerOffset(long consumerOffset) {
        this.consumerOffset = consumerOffset;
    }

    public long getPullOffset() {
        return pullOffset;
    }

    public void setPullOffset(long pullOffset) {
        this.pullOffset = pullOffset;
    }

    public long getLagEstimatedAccumulation() {
        return lagEstimatedAccumulation;
    }

    public void setLagEstimatedAccumulation(long lagEstimatedAccumulation) {
        this.lagEstimatedAccumulation = lagEstimatedAccumulation;
    }

    public long getInFlightMsgCountEstimatedAccumulation() {
        return inFlightMsgCountEstimatedAccumulation;
    }

    public void setInFlightMsgCountEstimatedAccumulation(long inFlightMsgCountEstimatedAccumulation) {
        this.inFlightMsgCountEstimatedAccumulation = inFlightMsgCountEstimatedAccumulation;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public long getEarliestUnconsumedTimestamp() {
        return earliestUnconsumedTimestamp;
    }

    public void setEarliestUnconsumedTimestamp(long earliestUnconsumedTimestamp) {
        this.earliestUnconsumedTimestamp = earliestUnconsumedTimestamp;
    }

    public long getEarliestUnPulledTimestamp() {
        return earliestUnPulledTimestamp;
    }

    public void setEarliestUnPulledTimestamp(long earliestUnPulledTimestamp) {
        this.earliestUnPulledTimestamp = earliestUnPulledTimestamp;
    }
}
