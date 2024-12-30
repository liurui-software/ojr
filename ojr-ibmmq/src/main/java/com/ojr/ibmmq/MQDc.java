package com.ojr.ibmmq;

import com.ojr.core.AbstractDc;
import com.ojr.core.DcUtil;
import com.ojr.core.ResourceEnricher;
import com.ojr.core.metric.RawMetric;
import com.ojr.ibmmq.mqclient.DataQuerier;
import com.ojr.ibmmq.mqclient.MQClient;
import com.ojr.ibmmq.mqclient.queriers.ChannelQuerier;
import com.ojr.ibmmq.mqclient.queriers.ListenerQuerier;
import com.ojr.ibmmq.mqclient.queriers.QmgrQuerier;
import com.ojr.ibmmq.mqclient.queriers.QueueQuerier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ojr.ibmmq.MQDcUtil.*;

/**
 * Represents a data collector specific to IBM MQ, extending the functionality of an abstract data collector.
 * This class is configured using {@link MQDcConfig} and is responsible for handling IBM MQ related data collection tasks.
 */
public class MQDc extends AbstractDc<MQDcConfig> {
    private static final Logger logger = Logger.getLogger(MQDc.class.getName());

    private String qmgr;
    private boolean isLocal;
    private String user;
    private String password;
    private String host;
    private int port;
    private String channel;
    private String queuesMonitored;
    private String customEventQueues;
    private String keystore;
    private String keystorePassword;
    private String cipherSuite;

    private MQClient mqClient;
    private List<DataQuerier> queriers = new ArrayList<>();

    @Override
    public Map<String, RawMetric> provideInitRawMetricsMap() {
        return new MQRawMetricRegistry().getMap();
    }

    public void readExtraParameters(Map<String, Object> properties, MQDcConfig MQDcConfig) {
        qmgr = (String) properties.get(PARAM_QUEUE_MANAGER);
        isLocal = (boolean) properties.getOrDefault(PARAM_IS_LOCAL, false);
        user = (String) properties.get(MQDcUtil.PARAM_USER);
        password = (String) properties.get(MQDcUtil.PARAM_PASSWORD);
        host = (String) properties.get(MQDcUtil.PARAM_HOST);
        port = (Integer) properties.getOrDefault(PARAM_PORT, 1414);
        channel = (String) properties.getOrDefault(PARAM_CHANNEL, "SYSTEM.ADMIN.SVRCONN");
        queuesMonitored = (String) properties.getOrDefault(PARAM_QUEUES_MONITORED, "*");
        customEventQueues = (String) properties.get(PARAM_CUSTOM_EVENT_QUEUES);
        keystore = (String) properties.get(PARAM_KEYSTORE);
        keystorePassword = (String) properties.get(PARAM_KEYSTORE_PASSWORD);
        cipherSuite = (String) properties.get(PARAM_CIPHER_SUITE);
    }

    @Override
    public void processParameters(Map<String, Object> properties, MQDcConfig MQDcConfig) throws Exception {
        mqClient = new MQClient(this);
        registerQuerier(new QmgrQuerier(mqClient));
        registerQuerier(new QueueQuerier(mqClient, queuesMonitored));
        registerQuerier(new ListenerQuerier(mqClient));
        registerQuerier(new ChannelQuerier(mqClient));
    }

    @Override
    public void enrichResourceAttributes(ResourceEnricher enricher) {
        enricher.enrich(DcUtil.OJR_PLUGIN, "ibmmq");
        enricher.enrich(PARAM_QUEUE_MANAGER, qmgr);
        if (isLocal) {
            this.enrichHostNameInResource(enricher);
        }
    }

    @Override
    public Predicate<String> updateResAttrsFilterForPrometheus(Predicate<String> oldFilter) {
        return oldFilter.or(key -> key.equals(PARAM_QUEUE_MANAGER));
    }

    @Override
    public void collectData() {
        logger.info("Start to collect metrics");
        try {
            mqClient.connect();
            for (DataQuerier querier : queriers) {
                querier.collectData();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "collectData failed!", e);
            mqClient.disconnect();
        }
    }

    public void registerQuerier(DataQuerier querier) {
        queriers.add(querier);
    }

    /**
     * Getters and Setters
     */

    public String getQmgr() {
        return qmgr;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getChannel() {
        return channel;
    }

    public String getQueuesMonitored() {
        return queuesMonitored;
    }

    public String getCustomEventQueues() {
        return customEventQueues;
    }

    public String getKeystore() {
        return keystore;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getCipherSuite() {
        return cipherSuite;
    }
}
