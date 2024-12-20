package com.ojr.ibmmq;

import com.ojr.core.metric.RawMetric;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ojr.core.metric.MetricInstrumentType.GAUGE;
import static com.ojr.ibmmq.MQDcUtil.*;


public class MQRawMetricRegistry {
    private final Map<String, RawMetric> map = new ConcurrentHashMap<String, RawMetric>() {
        {
            put(QMGR_META_NAME, new RawMetric(GAUGE, QMGR_META_NAME, QMGR_META_DESC, QMGR_META_UNIT, true, null));
        }
    };

    public Map<String, RawMetric> getMap() {
        return map;
    }
}
