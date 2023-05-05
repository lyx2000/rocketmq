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

package org.apache.rocketmq.proxy;

import com.google.common.collect.Lists;
import io.grpc.protobuf.services.ChannelzService;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.broker.BrokerController;
import org.apache.rocketmq.broker.BrokerStartup;
import org.apache.rocketmq.broker.client.ConsumerGroupInfo;
import org.apache.rocketmq.broker.eventtrack.EventTrackerManager;
import org.apache.rocketmq.broker.eventtrack.EventTrackerUtil;
import org.apache.rocketmq.broker.eventtrack.EventType;
import org.apache.rocketmq.broker.eventtrack.listener.ConsumerEventTrackListener;
import org.apache.rocketmq.broker.eventtrack.listener.ProducerEventTrackListener;
import org.apache.rocketmq.broker.eventtrack.listener.SubscriptionEventTrackListener;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.constant.LoggerName;
import org.apache.rocketmq.common.thread.ThreadPoolMonitor;
import org.apache.rocketmq.logging.org.slf4j.Logger;
import org.apache.rocketmq.logging.org.slf4j.LoggerFactory;
import org.apache.rocketmq.proxy.common.AbstractStartAndShutdown;
import org.apache.rocketmq.proxy.common.StartAndShutdown;
import org.apache.rocketmq.proxy.config.Configuration;
import org.apache.rocketmq.proxy.config.ConfigurationManager;
import org.apache.rocketmq.proxy.config.ProxyConfig;
import org.apache.rocketmq.proxy.grpc.GrpcServer;
import org.apache.rocketmq.proxy.grpc.GrpcServerBuilder;
import org.apache.rocketmq.proxy.grpc.v2.GrpcMessagingApplication;
import org.apache.rocketmq.proxy.metrics.ProxyMetricsManager;
import org.apache.rocketmq.proxy.processor.DefaultMessagingProcessor;
import org.apache.rocketmq.proxy.processor.MessagingProcessor;
import org.apache.rocketmq.proxy.remoting.RemotingProtocolServer;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;
import org.apache.rocketmq.srvutil.ServerUtil;

import static org.apache.rocketmq.proxy.metrics.ProxyMetricsConstant.NODE_TYPE_PROXY;

public class ProxyStartup {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.PROXY_LOGGER_NAME);
    private static final ProxyStartAndShutdown PROXY_START_AND_SHUTDOWN = new ProxyStartAndShutdown();

    private static class ProxyStartAndShutdown extends AbstractStartAndShutdown {
        @Override
        public void shutdown() throws Exception {
            super.shutdown();
        }

        @Override
        public void appendStartAndShutdown(StartAndShutdown startAndShutdown) {
            super.appendStartAndShutdown(startAndShutdown);
        }
    }

    public static void main(String[] args) {
        try {
            // parse argument from command line
            CommandLineArgument commandLineArgument = parseCommandLineArgument(args);
            initConfiguration(commandLineArgument);

            // init thread pool monitor for proxy.
            initThreadPoolMonitor();

            ThreadPoolExecutor executor = createServerExecutor();

            MessagingProcessor messagingProcessor = createMessagingProcessor();

            // create grpcServer
            GrpcServer grpcServer = GrpcServerBuilder.newBuilder(executor, ConfigurationManager.getProxyConfig().getGrpcServerPort())
                .addService(createServiceProcessor(messagingProcessor))
                .addService(ChannelzService.newInstance(100))
                .addService(ProtoReflectionService.newInstance())
                .configInterceptor()
                .build();
            PROXY_START_AND_SHUTDOWN.appendStartAndShutdown(grpcServer);

            RemotingProtocolServer remotingServer = new RemotingProtocolServer(messagingProcessor);
            PROXY_START_AND_SHUTDOWN.appendStartAndShutdown(remotingServer);

            // start servers one by one.
            PROXY_START_AND_SHUTDOWN.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("try to shutdown server");
                try {
                    PROXY_START_AND_SHUTDOWN.preShutdown();
                    PROXY_START_AND_SHUTDOWN.shutdown();
                } catch (Exception e) {
                    log.error("err when shutdown rocketmq-proxy", e);
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("find an unexpect err.", e);
            System.exit(1);
        }

        System.out.printf("%s%n", new Date() + " rocketmq-proxy startup successfully");
        log.info(new Date() + " rocketmq-proxy startup successfully");
    }

    protected static void initConfiguration(CommandLineArgument commandLineArgument) throws Exception {
        if (StringUtils.isNotBlank(commandLineArgument.getProxyConfigPath())) {
            System.setProperty(Configuration.CONFIG_PATH_PROPERTY, commandLineArgument.getProxyConfigPath());
        }
        ConfigurationManager.initEnv();
        ConfigurationManager.intConfig();
        setConfigFromCommandLineArgument(commandLineArgument);
        log.info("Current configuration: " + ConfigurationManager.formatProxyConfig());

    }

    protected static CommandLineArgument parseCommandLineArgument(String[] args) {
        CommandLine commandLine = ServerUtil.parseCmdLine("mqproxy", args,
            buildCommandlineOptions(), new DefaultParser());
        if (commandLine == null) {
            throw new RuntimeException("parse command line argument failed");
        }

        CommandLineArgument commandLineArgument = new CommandLineArgument();
        MixAll.properties2Object(ServerUtil.commandLine2Properties(commandLine), commandLineArgument);
        return commandLineArgument;
    }

    private static Options buildCommandlineOptions() {
        Options options = ServerUtil.buildCommandlineOptions(new Options());

        Option opt = new Option("bc", "brokerConfigPath", true, "Broker config file path for local mode");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("pc", "proxyConfigPath", true, "Proxy config file path");
        opt.setRequired(false);
        options.addOption(opt);

        opt = new Option("pm", "proxyMode", true, "Proxy run in local or cluster mode");
        opt.setRequired(false);
        options.addOption(opt);

        return options;
    }

    private static void setConfigFromCommandLineArgument(CommandLineArgument commandLineArgument) {
        if (StringUtils.isNotBlank(commandLineArgument.getNamesrvAddr())) {
            ConfigurationManager.getProxyConfig().setNamesrvAddr(commandLineArgument.getNamesrvAddr());
        }
        if (StringUtils.isNotBlank(commandLineArgument.getBrokerConfigPath())) {
            ConfigurationManager.getProxyConfig().setBrokerConfigPath(commandLineArgument.getBrokerConfigPath());
        }
        if (StringUtils.isNotBlank(commandLineArgument.getProxyMode())) {
            ConfigurationManager.getProxyConfig().setProxyMode(commandLineArgument.getProxyMode());
        }
    }

    protected static MessagingProcessor createMessagingProcessor() {
        String proxyModeStr = ConfigurationManager.getProxyConfig().getProxyMode();
        MessagingProcessor messagingProcessor;

        if (ProxyMode.isClusterMode(proxyModeStr)) {
            messagingProcessor = DefaultMessagingProcessor.createForClusterMode();
            ProxyMetricsManager proxyMetricsManager = ProxyMetricsManager.initClusterMode(ConfigurationManager.getProxyConfig());
            PROXY_START_AND_SHUTDOWN.appendStartAndShutdown(proxyMetricsManager);

            if (ConfigurationManager.getProxyConfig().isEnableEventTrack()) {
                EventTrackerManager.initEventContext(ConfigurationManager.getProxyConfig().getProxyName(), NODE_TYPE_PROXY);
                Set<EventType> enableEventTypeSet = EventTrackerUtil.decodeEventList(ConfigurationManager.getProxyConfig().getEnabledTrackerList());
                if (EventTrackerUtil.checkEvent(EventType.CONSUMER_GROUP_CLIENT_EVENT, enableEventTypeSet)) {
                    messagingProcessor.registerConsumerListener(new ConsumerEventTrackListener());
                }
                if (EventTrackerUtil.checkEvent(EventType.PRODUCER_CLIENT_EVENT, enableEventTypeSet)) {
                    messagingProcessor.registerProducerListener(new ProducerEventTrackListener());
                }
                if (EventTrackerUtil.checkEvent(EventType.SUBSCRIPTION_EVENT, enableEventTypeSet)) {
                    ConsumerGroupInfo.appendSubscriptionChangeListener(new SubscriptionEventTrackListener());
                }
                PROXY_START_AND_SHUTDOWN.appendStartAndShutdown(new StartAndShutdown() {
                    @Override
                    public void shutdown() {
                        if (EventTrackerUtil.checkEvent(EventType.SHUTDOWN_EVENT, enableEventTypeSet)) {
                            EventTrackerManager.trackEvent(EventType.SHUTDOWN_EVENT);
                        }
                    }

                    @Override
                    public void start() {
                        if (EventTrackerUtil.checkEvent(EventType.STARTUP_EVENT, enableEventTypeSet)) {
                            EventTrackerManager.trackEvent(EventType.STARTUP_EVENT);
                        }
                    }
                });
            }

        } else if (ProxyMode.isLocalMode(proxyModeStr)) {
            BrokerController brokerController = createBrokerController();
            ProxyMetricsManager.initLocalMode(brokerController.getBrokerMetricsManager(), ConfigurationManager.getProxyConfig());
            StartAndShutdown brokerControllerWrapper = new StartAndShutdown() {
                @Override
                public void start() throws Exception {
                    brokerController.start();
                    String tip = "The broker[" + brokerController.getBrokerConfig().getBrokerName() + ", "
                        + brokerController.getBrokerAddr() + "] boot success. serializeType=" + RemotingCommand.getSerializeTypeConfigInThisServer();
                    if (null != brokerController.getBrokerConfig().getNamesrvAddr()) {
                        tip += " and name server is " + brokerController.getBrokerConfig().getNamesrvAddr();
                    }
                    log.info(tip);
                }

                @Override
                public void shutdown() throws Exception {
                    brokerController.shutdown();
                }
            };
            PROXY_START_AND_SHUTDOWN.appendStartAndShutdown(brokerControllerWrapper);
            messagingProcessor = DefaultMessagingProcessor.createForLocalMode(brokerController);
        } else {
            throw new IllegalArgumentException("try to start grpc server with wrong mode, use 'local' or 'cluster'");
        }
        PROXY_START_AND_SHUTDOWN.appendStartAndShutdown(messagingProcessor);
        return messagingProcessor;
    }

    private static GrpcMessagingApplication createServiceProcessor(MessagingProcessor messagingProcessor) {
        GrpcMessagingApplication application = GrpcMessagingApplication.create(messagingProcessor);
        PROXY_START_AND_SHUTDOWN.appendStartAndShutdown(application);
        return application;
    }

    protected static BrokerController createBrokerController() {
        ProxyConfig config = ConfigurationManager.getProxyConfig();
        List<String> brokerStartupArgList = Lists.newArrayList("-c", config.getBrokerConfigPath());
        if (StringUtils.isNotBlank(config.getNamesrvAddr())) {
            brokerStartupArgList.add("-n");
            brokerStartupArgList.add(config.getNamesrvAddr());
        }
        String[] brokerStartupArgs = brokerStartupArgList.toArray(new String[0]);
        return BrokerStartup.createBrokerController(brokerStartupArgs);
    }

    public static ThreadPoolExecutor createServerExecutor() {
        ProxyConfig config = ConfigurationManager.getProxyConfig();
        int threadPoolNums = config.getGrpcThreadPoolNums();
        int threadPoolQueueCapacity = config.getGrpcThreadPoolQueueCapacity();
        ThreadPoolExecutor executor = ThreadPoolMonitor.createAndMonitor(
            threadPoolNums,
            threadPoolNums,
            1, TimeUnit.MINUTES,
            "GrpcRequestExecutorThread",
            threadPoolQueueCapacity
        );
        PROXY_START_AND_SHUTDOWN.appendShutdown(executor::shutdown);
        return executor;
    }

    public static void initThreadPoolMonitor() {
        ProxyConfig config = ConfigurationManager.getProxyConfig();
        ThreadPoolMonitor.config(
            LoggerFactory.getLogger(LoggerName.PROXY_LOGGER_NAME),
            LoggerFactory.getLogger(LoggerName.PROXY_WATER_MARK_LOGGER_NAME),
            config.isEnablePrintJstack(), config.getPrintJstackInMillis(),
            config.getPrintThreadPoolStatusInMillis());
        ThreadPoolMonitor.init();
    }
}
