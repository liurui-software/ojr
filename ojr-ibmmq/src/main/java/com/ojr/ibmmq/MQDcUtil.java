package com.ojr.ibmmq;

import com.ojr.core.metric.MetricQueryResult;

public class MQDcUtil {
    /**
     * Metrics definitions:
     */
    public static final String UNIT_SEC = "s";
    public static final String UNIT_BYTE = "By";

    public static final String UNIT_STATUS = "{status}";
    public static final String UNIT_COUNT = "{count}";

    public static final String QMGR_META = "qmgr.metadata";
    public static final String QMGR_META_DESC = "The metadata of the queue manager";
    public static final String QMGR_META_UNIT = "{metadata}";
    public static final String VERSION = "version";
    public static final String PLATFORM = "platform";

    public static final String QMGR_CMD_LEVEL = "qmgr.cmd.level";
    public static final String QMGR_CMD_LEVEL_DESC = "The command level of the queue manager";
    public static final String QMGR_CMD_LEVEL_UNIT = "{level}";

    public static final String QMGR_MAX_HANDLES = "qmgr.max.handles";
    public static final String QMGR_MAX_HANDLES_DESC = "Maximum handles of the queue manager";
    public static final String QMGR_MAX_HANDLES_UNIT = UNIT_COUNT;

    public static final String QMGR_CONNECTION_COUNT = "qmgr.connection.count";
    public static final String QMGR_CONNECTION_COUNT_DESC = "The count of connections to the queue manager";
    public static final String QMGR_CONNECTION_COUNT_UNIT = UNIT_COUNT;

    public static final String QMGR_STATUS = "qmgr.status";
    public static final String QMGR_STATUS_DESC = "The status of the queue manager. (1: STARTING, 2: RUNNING, 3: QUIESCING, 4: STANDBY)";
    public static final String QMGR_STATUS_UNIT = UNIT_STATUS;

    public static final String QMGR_CHINIT_STATUS = "qmgr.chinit.status";
    public static final String QMGR_CHINIT_STATUS_DESC = "The status of the channel initiator of the queue manager" +
            "(0: INACTIVE, 1: STARTING, 2: RUNNING, 3: STOPPING, 4: RETRYING)";
    public static final String QMGR_CHINIT_STATUS_UNIT = UNIT_STATUS;

    public static final String QMGR_START_TIME = "qmgr.start.time";
    public static final String QMGR_START_TIME_DESC = "The time (Epoch second) when the queue manager was started";
    public static final String QMGR_START_TIME_UNIT = UNIT_SEC;

    public static final String QMGR_START_TIME_FORMATED = "qmgr.start.time.formated";
    public static final String QMGR_START_TIME_FORMATED_DESC = "The time (formated as \"yyyyMMdd.HHmmss\") when the queue manager was started";
    public static final String QMGR_START_TIME_FORMATED_UNIT = "{time}";

    public static final String QMGR_PUBSUB_STATUS = "qmgr.pubsub.status";
    public static final String QMGR_PUBSUB_STATUS_DESC = "The status of the pub/sub of the queue manager" +
            "(0: INACTIVE, 1: STARTING, 2: STOPPING, 3: ACTIVE, 4: COMPAT, 5: ERROR, 6: REFUSED)";
    public static final String QMGR_PUBSUB_STATUS_UNIT = UNIT_STATUS;

    public static final String QUEUE_META = "queue.metadata";
    public static final String QUEUE_META_DESC = "The metadata of the queue";
    public static final String QUEUE_META_UNIT = "{metadata}";
    public static final String QUEUE = "queue";
    public static final String TYPE = "type";
    public static final String DELIVERY = "delivery";
    public static final String USAGE = "usage";

    public static final String QUEUE_DEPTH = "queue.depth";
    public static final String QUEUE_DEPTH_DESC = "Current depth of the queue";
    public static final String QUEUE_DEPTH_UNIT = UNIT_COUNT;

    public static final String QUEUE_MAX_DEPTH = "queue.max.depth";
    public static final String QUEUE_MAX_DEPTH_DESC = "Maximum depth of the queue";
    public static final String QUEUE_MAX_DEPTH_UNIT = UNIT_COUNT;

    public static final String QUEUE_UNCOMMITED_MSG = "queue.uncommited.messages";
    public static final String QUEUE_UNCOMMITED_MSG_DESC = "Uncommited messages of the queue";
    public static final String QUEUE_UNCOMMITED_MSG_UNIT = UNIT_COUNT;

    public static final String QUEUE_OPEN_INPUT = "queue.open.input";
    public static final String QUEUE_OPEN_INPUT_DESC = "Number of open for input from the queue";
    public static final String QUEUE_OPEN_INPUT_UNIT = UNIT_COUNT;

    public static final String QUEUE_OPEN_OUTPUT = "queue.open.output";
    public static final String QUEUE_OPEN_OUTPUT_DESC = "Number of open for output to the queue";
    public static final String QUEUE_OPEN_OUTPUT_UNIT = UNIT_COUNT;

    public static final String QUEUE_ENQ_COUNT = "queue.enq.count";
    public static final String QUEUE_ENQ_COUNT_DESC = "The count of enqueue to the queue";
    public static final String QUEUE_ENQ_COUNT_UNIT = UNIT_COUNT;

    public static final String QUEUE_DEQ_COUNT = "queue.deq.count";
    public static final String QUEUE_DEQ_COUNT_DESC = "The count of dequeue from the queue";
    public static final String QUEUE_DEQ_COUNT_UNIT = UNIT_COUNT;


    /**
     * Query results:
     */

    public static MetricQueryResult getQmgrMetadataResult(String version, String platform) {
        MetricQueryResult result = new MetricQueryResult(1);
        result.setAttribute(VERSION, version);
        result.setAttribute(PLATFORM, platform);
        return result;
    }

    public static MetricQueryResult getQueueMetadataResult(String queue, String type, String delivery, String usage) {
        MetricQueryResult result = new MetricQueryResult(1);
        result.setAttribute(QUEUE, queue);
        result.setAttribute(TYPE, type);
        result.setAttribute(DELIVERY, delivery);
        result.setAttribute(USAGE, usage);
        result.setKey(queue);
        return result;
    }


    public static MetricQueryResult getSimpleQueueMetricResult(String queue, int depth) {
        MetricQueryResult result = new MetricQueryResult(depth);
        result.setAttribute(QUEUE, queue);
        result.setKey(queue);
        return result;
    }

    /**
     * Configuration Parameters:
     */
    public static final String PARAM_QUEUE_MANAGER = "queueManager";
    public static final String PARAM_IS_LOCAL = "isLocal";
    public static final String PARAM_USER = "user";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_HOST = "host";
    public static final String PARAM_PORT = "port";
    public static final String PARAM_CHANNEL = "channel";
    public static final String PARAM_QUEUES_MONITORED = "queuesMonitored";
    public static final String PARAM_CUSTOM_EVENT_QUEUES = "customEventQueues";
    public static final String PARAM_KEYSTORE = "keystore";
    public static final String PARAM_KEYSTORE_PASSWORD = "keystorePassword";
    public static final String PARAM_CIPHER_SUITE = "cipherSuite";
}