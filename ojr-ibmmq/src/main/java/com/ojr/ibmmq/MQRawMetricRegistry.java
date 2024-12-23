package com.ojr.ibmmq;

import com.ojr.core.metric.RawMetric;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ojr.core.metric.MetricInstrumentType.GAUGE;
import static com.ojr.ibmmq.MQDcUtil.*;
import static com.ojr.ibmmq.MQDcUtil.QUEUE_UNCOMMITED_MSG_UNIT;


public class MQRawMetricRegistry {
    private final Map<String, RawMetric> map = new ConcurrentHashMap<String, RawMetric>() {
        {
            //Qmgr:
            put(QMGR_META, new RawMetric(GAUGE, QMGR_META, QMGR_META_DESC, QMGR_META_UNIT, true, null));
            put(QMGR_CMD_LEVEL, new RawMetric(GAUGE, QMGR_CMD_LEVEL, QMGR_CMD_LEVEL_DESC, QMGR_CMD_LEVEL_UNIT, true, null));
            put(QMGR_MAX_HANDLES, new RawMetric(GAUGE, QMGR_MAX_HANDLES, QMGR_MAX_HANDLES_DESC, QMGR_MAX_HANDLES_UNIT, true, null));

            put(QMGR_CONNECTION_COUNT, new RawMetric(GAUGE, QMGR_CONNECTION_COUNT, QMGR_CONNECTION_COUNT_DESC, QMGR_CONNECTION_COUNT_UNIT, true, null));
            put(QMGR_STATUS, new RawMetric(GAUGE, QMGR_STATUS, QMGR_STATUS_DESC, QMGR_STATUS_UNIT, true, null));
            put(QMGR_CHINIT_STATUS, new RawMetric(GAUGE, QMGR_CHINIT_STATUS, QMGR_CHINIT_STATUS_DESC, QMGR_CHINIT_STATUS_UNIT, true, null));
            put(QMGR_START_TIME, new RawMetric(GAUGE, QMGR_START_TIME, QMGR_START_TIME_DESC, QMGR_START_TIME_UNIT, true, null));
            put(QMGR_START_TIME_FORMATED, new RawMetric(GAUGE, QMGR_START_TIME_FORMATED, QMGR_START_TIME_FORMATED_DESC, QMGR_START_TIME_FORMATED_UNIT, false, null));

            put(QMGR_PUBSUB_STATUS, new RawMetric(GAUGE, QMGR_PUBSUB_STATUS, QMGR_PUBSUB_STATUS_DESC, QMGR_PUBSUB_STATUS_UNIT, true, null));

            //Queue:
            put(QUEUE_META, new RawMetric(GAUGE, QUEUE_META, QUEUE_META_DESC, QUEUE_META_UNIT, true, null));
            put(QUEUE_DEPTH, new RawMetric(GAUGE, QUEUE_DEPTH, QUEUE_DEPTH_DESC, QUEUE_DEPTH_UNIT, true, null));
            put(QUEUE_MAX_DEPTH, new RawMetric(GAUGE, QUEUE_MAX_DEPTH, QUEUE_MAX_DEPTH_DESC, QUEUE_MAX_DEPTH_UNIT, true, null));

            put(QUEUE_UNCOMMITED_MSG, new RawMetric(GAUGE, QUEUE_UNCOMMITED_MSG, QUEUE_UNCOMMITED_MSG_DESC, QUEUE_UNCOMMITED_MSG_UNIT, true, null));
            put(QUEUE_OPEN_INPUT, new RawMetric(GAUGE, QUEUE_OPEN_INPUT, QUEUE_OPEN_INPUT_DESC, QUEUE_OPEN_INPUT_UNIT, true, null));
            put(QUEUE_OPEN_OUTPUT, new RawMetric(GAUGE, QUEUE_OPEN_OUTPUT, QUEUE_OPEN_OUTPUT_DESC, QUEUE_OPEN_OUTPUT_UNIT, true, null));

            put(QUEUE_ENQ_COUNT, new RawMetric(GAUGE, QUEUE_ENQ_COUNT, QUEUE_ENQ_COUNT_DESC, QUEUE_ENQ_COUNT_UNIT, true, null));
            put(QUEUE_DEQ_COUNT, new RawMetric(GAUGE, QUEUE_DEQ_COUNT, QUEUE_DEQ_COUNT_DESC, QUEUE_DEQ_COUNT_UNIT, true, null));
        }
    };

    public Map<String, RawMetric> getMap() {
        return map;
    }
}
