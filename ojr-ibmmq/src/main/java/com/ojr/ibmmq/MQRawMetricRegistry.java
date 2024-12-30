package com.ojr.ibmmq;

import com.ojr.core.metric.RawMetric;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ojr.core.metric.MetricInstrumentType.COUNTER;
import static com.ojr.core.metric.MetricInstrumentType.GAUGE;
import static com.ojr.ibmmq.MQDcUtil.*;


public class MQRawMetricRegistry {
    private final Map<String, RawMetric> map = new ConcurrentHashMap<String, RawMetric>() {
        {
            //Queue Manager:
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
            put(QUEUE_META, new RawMetric(GAUGE, QUEUE_META, QUEUE_META_DESC, QUEUE_META_UNIT, true, QUEUE));
            put(QUEUE_DEPTH, new RawMetric(GAUGE, QUEUE_DEPTH, QUEUE_DEPTH_DESC, QUEUE_DEPTH_UNIT, true, QUEUE));
            put(QUEUE_MAX_DEPTH, new RawMetric(GAUGE, QUEUE_MAX_DEPTH, QUEUE_MAX_DEPTH_DESC, QUEUE_MAX_DEPTH_UNIT, true, QUEUE));

            put(QUEUE_UNCOMMITED_MSG, new RawMetric(GAUGE, QUEUE_UNCOMMITED_MSG, QUEUE_UNCOMMITED_MSG_DESC, QUEUE_UNCOMMITED_MSG_UNIT, true, QUEUE));
            put(QUEUE_OPEN_INPUT, new RawMetric(GAUGE, QUEUE_OPEN_INPUT, QUEUE_OPEN_INPUT_DESC, QUEUE_OPEN_INPUT_UNIT, true, QUEUE));
            put(QUEUE_OPEN_OUTPUT, new RawMetric(GAUGE, QUEUE_OPEN_OUTPUT, QUEUE_OPEN_OUTPUT_DESC, QUEUE_OPEN_OUTPUT_UNIT, true, QUEUE));

            put(QUEUE_ENQ_COUNT, new RawMetric(GAUGE, QUEUE_ENQ_COUNT, QUEUE_ENQ_COUNT_DESC, QUEUE_ENQ_COUNT_UNIT, true, QUEUE));
            put(QUEUE_DEQ_COUNT, new RawMetric(GAUGE, QUEUE_DEQ_COUNT, QUEUE_DEQ_COUNT_DESC, QUEUE_DEQ_COUNT_UNIT, true, QUEUE));

            //Listener:
            put(LISTENER_META, new RawMetric(GAUGE, LISTENER_META, LISTENER_META_DESC, LISTENER_META_UNIT, true, LISTENER));
            put(LISTENER_STATUS, new RawMetric(GAUGE, LISTENER_STATUS, LISTENER_STATUS_DESC, LISTENER_STATUS_UNIT, true, LISTENER));

            //Channel:
            put(CHANNEL_STATUS, new RawMetric(GAUGE, CHANNEL_STATUS, CHANNEL_STATUS_DESC, CHANNEL_STATUS_UNIT, true, CHANNEL));
            put(CHANNEL_INDOUBT_STATUS, new RawMetric(GAUGE, CHANNEL_INDOUBT_STATUS, CHANNEL_INDOUBT_STATUS_DESC, CHANNEL_INDOUBT_STATUS_UNIT, true, CHANNEL));
            put(CHANNEL_TYPE, new RawMetric(GAUGE, CHANNEL_TYPE, CHANNEL_TYPE_DESC, CHANNEL_TYPE_UNIT, true, CHANNEL));

            put(CHANNEL_BUFFERS_SENT, new RawMetric(COUNTER, CHANNEL_BUFFERS_SENT, CHANNEL_BUFFERS_SENT_DESC, CHANNEL_BUFFERS_SENT_UNIT, true, CHANNEL));
            put(CHANNEL_BUFFERS_RECEIVED, new RawMetric(COUNTER, CHANNEL_BUFFERS_RECEIVED, CHANNEL_BUFFERS_RECEIVED_DESC, CHANNEL_BUFFERS_RECEIVED_UNIT, true, CHANNEL));
            put(CHANNEL_BYTES_SENT, new RawMetric(COUNTER, CHANNEL_BYTES_SENT, CHANNEL_BYTES_SENT_DESC, CHANNEL_BYTES_SENT_UNIT, true, CHANNEL));
            put(CHANNEL_BYTES_RECEIVED, new RawMetric(COUNTER, CHANNEL_BYTES_RECEIVED, CHANNEL_BYTES_RECEIVED_DESC, CHANNEL_BYTES_RECEIVED_UNIT, true, CHANNEL));
        }
    };

    public Map<String, RawMetric> getMap() {
        return map;
    }
}
