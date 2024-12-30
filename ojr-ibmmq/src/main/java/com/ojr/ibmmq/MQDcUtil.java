package com.ojr.ibmmq;

import com.ojr.core.metric.MetricQueryResult;

public class MQDcUtil {

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


    /**
     * Metrics definitions:
     */
    public static final String UNIT_SEC = "s";
    public static final String UNIT_BYTE = "By";

    public static final String UNIT_STATUS = "{status}";
    public static final String UNIT_COUNT = "{count}";

    public static final String QMGR_META = "ibmmq.qmgr.metadata";
    public static final String QMGR_META_DESC = "The metadata of the queue manager";
    public static final String QMGR_META_UNIT = "{metadata}";
    public static final String VERSION = "version";
    public static final String PLATFORM = "platform";

    public static final String QMGR_CMD_LEVEL = "ibmmq.qmgr.cmd.level";
    public static final String QMGR_CMD_LEVEL_DESC = "The command level of the queue manager";
    public static final String QMGR_CMD_LEVEL_UNIT = "{level}";

    public static final String QMGR_MAX_HANDLES = "ibmmq.qmgr.max.handles";
    public static final String QMGR_MAX_HANDLES_DESC = "Maximum handles of the queue manager";
    public static final String QMGR_MAX_HANDLES_UNIT = UNIT_COUNT;

    public static final String QMGR_CONNECTION_COUNT = "ibmmq.qmgr.connection.count";
    public static final String QMGR_CONNECTION_COUNT_DESC = "The count of connections to the queue manager";
    public static final String QMGR_CONNECTION_COUNT_UNIT = UNIT_COUNT;

    public static final String QMGR_STATUS = "ibmmq.qmgr.status";
    public static final String QMGR_STATUS_DESC = "The status of the queue manager. (1: STARTING, 2: RUNNING, 3: QUIESCING, 4: STANDBY)";
    public static final String QMGR_STATUS_UNIT = UNIT_STATUS;

    public static final String QMGR_CHINIT_STATUS = "ibmmq.qmgr.chinit.status";
    public static final String QMGR_CHINIT_STATUS_DESC = "The status of the channel initiator of the queue manager" +
            " (0: INACTIVE, 1: STARTING, 2: RUNNING, 3: STOPPING, 4: RETRYING)";
    public static final String QMGR_CHINIT_STATUS_UNIT = UNIT_STATUS;

    public static final String QMGR_START_TIME = "ibmmq.qmgr.start.time";
    public static final String QMGR_START_TIME_DESC = "The time (Epoch second) when the queue manager was started";
    public static final String QMGR_START_TIME_UNIT = UNIT_SEC;

    public static final String QMGR_START_TIME_FORMATED = "ibmmq.qmgr.start.time.formated";
    public static final String QMGR_START_TIME_FORMATED_DESC = "The time (formated as \"yyyyMMdd.HHmmss\") when the queue manager was started";
    public static final String QMGR_START_TIME_FORMATED_UNIT = "{time}";

    public static final String QMGR_PUBSUB_STATUS = "ibmmq.qmgr.pubsub.status";
    public static final String QMGR_PUBSUB_STATUS_DESC = "The status of the pub/sub of the queue manager" +
            " (0: INACTIVE, 1: STARTING, 2: STOPPING, 3: ACTIVE, 4: COMPAT, 5: ERROR, 6: REFUSED)";
    public static final String QMGR_PUBSUB_STATUS_UNIT = UNIT_STATUS;

    public static final String QUEUE_META = "ibmmq.queue.metadata";
    public static final String QUEUE_META_DESC = "The metadata of the queue";
    public static final String QUEUE_META_UNIT = "{metadata}";
    public static final String QUEUE = "queue";
    public static final String TYPE = "type";
    public static final String DELIVERY = "delivery";
    public static final String USAGE = "usage";

    public static final String QUEUE_DEPTH = "ibmmq.queue.depth";
    public static final String QUEUE_DEPTH_DESC = "Current depth of the queue";
    public static final String QUEUE_DEPTH_UNIT = UNIT_COUNT;

    public static final String QUEUE_MAX_DEPTH = "ibmmq.queue.max.depth";
    public static final String QUEUE_MAX_DEPTH_DESC = "Maximum depth of the queue";
    public static final String QUEUE_MAX_DEPTH_UNIT = UNIT_COUNT;

    public static final String QUEUE_UNCOMMITED_MSG = "ibmmq.queue.uncommited.messages";
    public static final String QUEUE_UNCOMMITED_MSG_DESC = "Uncommited messages of the queue";
    public static final String QUEUE_UNCOMMITED_MSG_UNIT = UNIT_COUNT;

    public static final String QUEUE_OPEN_INPUT = "ibmmq.queue.open.input";
    public static final String QUEUE_OPEN_INPUT_DESC = "Number of open for input from the queue";
    public static final String QUEUE_OPEN_INPUT_UNIT = UNIT_COUNT;

    public static final String QUEUE_OPEN_OUTPUT = "ibmmq.queue.open.output";
    public static final String QUEUE_OPEN_OUTPUT_DESC = "Number of open for output to the queue";
    public static final String QUEUE_OPEN_OUTPUT_UNIT = UNIT_COUNT;

    public static final String QUEUE_ENQ_COUNT = "ibmmq.queue.enq.count";
    public static final String QUEUE_ENQ_COUNT_DESC = "The count of enqueue to the queue";
    public static final String QUEUE_ENQ_COUNT_UNIT = UNIT_COUNT;

    public static final String QUEUE_DEQ_COUNT = "ibmmq.queue.deq.count";
    public static final String QUEUE_DEQ_COUNT_DESC = "The count of dequeue from the queue";
    public static final String QUEUE_DEQ_COUNT_UNIT = UNIT_COUNT;

    public static final String LISTENER_META = "ibmmq.listener.metadata";
    public static final String LISTENER_META_DESC = "The metadata of the listener";
    public static final String LISTENER_META_UNIT = "{metadata}";
    public static final String LISTENER = "listener";
    public static final String IP = "ip";
    public static final String PORT = "port";

    public static final String LISTENER_STATUS = "ibmmq.listener.status";
    public static final String LISTENER_STATUS_DESC = "The status of the listener" +
            " (0: Stopped, 1: Quiescing, 2: Running, 3: Stopping, 4: Retrying)";
    public static final String LISTENER_STATUS_UNIT = "{status}";

    public static final String CHANNEL_STATUS = "ibmmq.channel.status";
    public static final String CHANNEL_STATUS_DESC = "The status of the channel" +
            " (0: Inactive, 1: Binding, 2: Quiescing, 3: Running, 4: Stopping, 5: Retrying, 6: Stopped, 7: Requesting, 8: Paused, 13: Initializing, 14: Switching)";
    public static final String CHANNEL_STATUS_UNIT = "{status}";
    public static final String CHANNEL = "channel";

    public static final String CHANNEL_INDOUBT_STATUS = "ibmmq.channel.indoubt.status";
    public static final String CHANNEL_INDOUBT_STATUS_DESC = "The in-doubt status of the channel (0: No, 1: Yes)";
    public static final String CHANNEL_INDOUBT_STATUS_UNIT = "{status}";

    public static final String CHANNEL_TYPE = "ibmmq.channel.type";
    public static final String CHANNEL_TYPE_DESC = "The type of the channel" +
            " (1: Sender, 2: Server, 3: Receiver, 4: Requester, 5: All, 6: Client connection, 7: Server connection, 8: Cluster receiver, 9: Cluster sender, 10: Telemetry channel, 11: AMQP)";
    public static final String CHANNEL_TYPE_UNIT = "{type}";

    public static final String CHANNEL_BUFFERS_SENT = "ibmmq.channel.buffers.sent";
    public static final String CHANNEL_BUFFERS_SENT_DESC = "The number of buffers sent by the channel";
    public static final String CHANNEL_BUFFERS_SENT_UNIT = UNIT_COUNT;

    public static final String CHANNEL_BUFFERS_RECEIVED = "ibmmq.channel.buffers.received";
    public static final String CHANNEL_BUFFERS_RECEIVED_DESC = "The number of buffers received by the channel";
    public static final String CHANNEL_BUFFERS_RECEIVED_UNIT = UNIT_COUNT;

    public static final String CHANNEL_BYTES_SENT = "ibmmq.channel.bytes.sent";
    public static final String CHANNEL_BYTES_SENT_DESC = "The number of bytes sent by the channel";
    public static final String CHANNEL_BYTES_SENT_UNIT = UNIT_COUNT;

    public static final String CHANNEL_BYTES_RECEIVED = "ibmmq.channel.bytes.received";
    public static final String CHANNEL_BYTES_RECEIVED_DESC = "The number of bytes received by the channel";
    public static final String CHANNEL_BYTES_RECEIVED_UNIT = UNIT_COUNT;


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

    public static MetricQueryResult getSimpleQueueMetricResult(String queue, int value) {
        MetricQueryResult result = new MetricQueryResult(value);
        result.setAttribute(QUEUE, queue);
        result.setKey(queue);
        return result;
    }

    public static MetricQueryResult getListenerMetadataResult(String listener, String ip, int port) {
        MetricQueryResult result = new MetricQueryResult(1);
        result.setAttribute(LISTENER, listener);
        result.setAttribute(IP, ip);
        result.setAttribute(PORT, port);
        result.setKey(listener);
        return result;
    }

    public static MetricQueryResult getSimpleListenerMetricResult(String listener, int value) {
        MetricQueryResult result = new MetricQueryResult(value);
        result.setAttribute(LISTENER, listener);
        result.setKey(listener);
        return result;
    }

    public static MetricQueryResult getSimpleChannelMetricResult(String channel, int value) {
        MetricQueryResult result = new MetricQueryResult(value);
        result.setAttribute(CHANNEL, channel);
        result.setKey(channel);
        return result;
    }

}