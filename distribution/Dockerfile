FROM openjdk:17-jdk-slim

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl unzip procps vim inetutils-ping && apt-get clean

ENV ROCKETMQ_VERSION 5.1.1-SNAPSHOT
ENV ROCKETMQ_HOME  /home/rocketmq/rocketmq-${ROCKETMQ_VERSION}

WORKDIR  ${ROCKETMQ_HOME}

COPY target/rocketmq-${ROCKETMQ_VERSION}/rocketmq-${ROCKETMQ_VERSION} .

RUN echo "alias ll='ls -l'" >> /etc/bash.bashrc

# export Java options
RUN export JAVA_OPT=" -Duser.home=/opt"

# Add ${JAVA_HOME}/lib/ext as java.ext.dirs
RUN sed -i 's/${JAVA_HOME}\/jre\/lib\/ext/${JAVA_HOME}\/jre\/lib\/ext:${JAVA_HOME}\/lib\/ext/' ${ROCKETMQ_HOME}/bin/tools.sh

WORKDIR ${ROCKETMQ_HOME}/bin
