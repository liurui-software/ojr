package com.ojr.mqappliance;

import com.ojr.core.DcUtil;
import com.ojr.core.ResourceEnricher;
import com.ojr.core.metric.RawMetric;
import com.ojr.host.AbstractHostDc;
import com.ojr.host.HostDcConfig;
import io.opentelemetry.semconv.incubating.HostIncubatingAttributes;
import io.opentelemetry.semconv.incubating.OsIncubatingAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.ojr.core.metric.MetricInstrumentType.GAUGE;
import static com.ojr.host.HostDcUtil.*;
import static com.ojr.mqappliance.MQApplianceUtil.*;


public class MQApplianceDc extends AbstractHostDc<HostDcConfig> {
    private static final Logger logger = Logger.getLogger(MQApplianceDc.class.getName());

    protected String applianceHost;
    private String applianceUser;
    private String appliancePassword;

    private Process process;
    protected BufferedReader bufferedReader;

    private long cpuTime = 0;
    private long cpuTimeIdle = 0;

    @Override
    public Map<String, RawMetric> provideInitRawMetricsMap() {
        Map<String, RawMetric> map = super.provideInitRawMetricsMap();
        map.put(SYSTEM_IBMQMGR_STATUS_NAME, new RawMetric(GAUGE, SYSTEM_IBMQMGR_STATUS_NAME, SYSTEM_IBMQMGR_STATUS_DESC, SYSTEM_IBMQMGR_STATUS_UNIT, true, "qmgr"));
        return map;
    }

    public void readExtraParameters(Map<String, Object> properties, HostDcConfig hostDcConfig) {
        super.readExtraParameters(properties, hostDcConfig);
        applianceHost = (String) properties.get(APPLIANCE_HOST);
        setServiceInstanceId(applianceHost);
        applianceUser = (String) properties.get(APPLIANCE_USER);
        appliancePassword = (String) properties.get(APPLIANCE_PASSWORD);
    }

    @Override
    public void processParameters(Map<String, Object> properties, HostDcConfig hostDcConfig) throws Exception {
    }

    @Override
    public void enrichResourceAttributes(ResourceEnricher enricher) {
        super.enrichResourceAttributes(enricher);
        enricher.enrich(DcUtil.OJR_PLUGIN, "mq-appliance");
        enricher.enrich(HostIncubatingAttributes.HOST_NAME, applianceHost);
        enricher.enrich(OsIncubatingAttributes.OS_TYPE, "MQ Appliance");
        enricher.enrich(HostIncubatingAttributes.HOST_ID, applianceHost);
    }

    @Override
    public void collectData() {
        logger.info("Start to collect metrics");
        try {
            String line = bufferedReader.readLine();
            if (line != null) {
                logger.info("Data collected: " + line);
                String[] tokens = line.split(";");
                if (tokens.length >= 7) {
                    String[] systemMetricsTokens = tokens[0].split(":");
                    if (systemMetricsTokens.length == 6) {
                        long cpuUsage = Long.parseLong(systemMetricsTokens[0]);
                        if (cpuUsage >= 0) {
                            long cpuTime1 = cpuTime + cpuUsage;
                            long cpuTimeIdle1 = cpuTimeIdle + (100 - cpuUsage);
                            if (cpuTime1 >= 0 && cpuTimeIdle1 >= 0) {
                                cpuTime = cpuTime1;
                                cpuTimeIdle = cpuTimeIdle1;
                            } else { //Reset
                                cpuTime = 0;
                                cpuTimeIdle = 0;
                            }
                            getRawMetric(SYSTEM_CPU_TIME_NAME).setValue(MQApplianceUtil.getApplianceCpuUsageResults(cpuTime, cpuTimeIdle));
                        }
                        getRawMetric(SYSTEM_CPU_LOAD1_NAME).setValue(Double.parseDouble(systemMetricsTokens[1]));
                        getRawMetric(SYSTEM_CPU_LOAD5_NAME).setValue(Double.parseDouble(systemMetricsTokens[2]));
                        getRawMetric(SYSTEM_CPU_LOAD15_NAME).setValue(Double.parseDouble(systemMetricsTokens[3]));
                        getRawMetric(SYSTEM_MEMORY_USAGE_NAME).setValue(MQApplianceUtil.getApplianceMemUsageResults(Long.parseLong(systemMetricsTokens[4]), Long.parseLong(systemMetricsTokens[5])));
                    }
                    getRawMetric(SYSTEM_NETWORK_CONNECTIONS_NAME).setValue(MQApplianceUtil.getApplianceNetworkConnectionsResults(tokens[1]));
                    getRawMetric(SYSTEM_NETWORK_IO_NAME).setValue(MQApplianceUtil.getApplianceNetworkInterfaceResults(tokens[2]));
                    getRawMetric(SYSTEM_NETWORK_PACKETS_NAME).setValue(MQApplianceUtil.getApplianceNetworkInterfaceResults(tokens[3]));
                    getRawMetric(SYSTEM_NETWORK_ERRORS_NAME).setValue(MQApplianceUtil.getApplianceNetworkInterfaceResults(tokens[4]));
                    getRawMetric(SYSTEM_NETWORK_DROPPED_NAME).setValue(MQApplianceUtil.getApplianceNetworkInterfaceResults(tokens[5]));
                    getRawMetric(SYSTEM_IBMQMGR_STATUS_NAME).setValue(MQApplianceUtil.getQmgrStatusResults(tokens[6]));
                } else {
                    logger.severe("Incorrect data format, cannot parse it.");
                }
            } else {
                logger.severe("No data returned. Please check if the appliance is running, or ensure that you are using the correct username and password.");
            }
        } catch (IOException e) {
            logger.severe("Cannot record loads: " + e.getMessage());
        }
    }

    @Override
    public void start() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("expect", "scripts/getMqApplianceData.exp", applianceHost, applianceUser, appliancePassword, String.valueOf(getPollInterval()));
            process = processBuilder.start();
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            addShutdownHook();
            exec.scheduleWithFixedDelay(this::collectData, 1, getPollInterval(), TimeUnit.SECONDS);
        } catch (IOException e) {
            logger.severe("Cannot start the data collector: " + e.getMessage());
        }
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::closeProcess));
    }

    private void closeProcess() {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            logger.severe("Cannot close the bufferedReader: " + e.getMessage());
        }
        if (process != null) {
            process.destroy();
        }
        if (!exec.isShutdown()) {
            exec.shutdownNow();
        }
    }

    private final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();

}
