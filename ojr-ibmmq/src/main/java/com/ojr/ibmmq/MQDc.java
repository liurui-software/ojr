package com.ojr.ibmmq;

import com.ojr.core.AbstractDc;
import com.ojr.core.DcUtil;
import com.ojr.core.ResourceEnricher;
import com.ojr.core.metric.RawMetric;

import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static com.ojr.ibmmq.MQDcUtil.*;


public class MQDc extends AbstractDc<MQDcConfig> {
    private static final Logger logger = Logger.getLogger(MQDc.class.getName());

    private String queueManager;
    private String user;
    private String password;

    @Override
    public Map<String, RawMetric> provideInitRawMetricsMap() {
        return new MQRawMetricRegistry().getMap();
    }

    public void readExtraParameters(Map<String, Object> properties, MQDcConfig MQDcConfig) {
        queueManager = (String) properties.get(PARAM_QUEUE_MANAGER);
        user = (String) properties.get(MQDcUtil.PARAM_USER);
        password = (String) properties.get(MQDcUtil.PARAM_PASSWORD);
    }

    @Override
    public void processParameters(Map<String, Object> properties, MQDcConfig MQDcConfig) throws Exception {
    }

    @Override
    public void enrichResourceAttributes(ResourceEnricher enricher) {
        enricher.enrich(DcUtil.OJR_PLUGIN, "ibmmq");
        enricher.enrich(PARAM_QUEUE_MANAGER, queueManager);
    }

    @Override
    public Predicate<String> updateResAttrsFilterForPrometheus(Predicate<String> oldFilter) {
        return oldFilter.or(key -> key.equals(PARAM_QUEUE_MANAGER));
    }

    @Override
    public void collectData() {
        logger.info("Start to collect metrics");
        getRawMetric(QMGR_META_NAME).setValue(MQDcUtil.getQmgrMetaResults(queueManager));
    }

}
