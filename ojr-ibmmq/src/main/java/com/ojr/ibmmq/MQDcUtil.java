package com.ojr.ibmmq;

import com.ojr.core.metric.MetricQueryResult;

public class MQDcUtil {
    public static final String UNIT_S = "s";
    public static final String UNIT_BY = "By";
    public static final String UNIT_1 = "1";

    public static final String QMGR_META_NAME = "qmgr.metadata";
    public static final String QMGR_META_DESC = "The metadata of the queue manager";
    public static final String QMGR_META_UNIT = "{status}";

    public static final String PARAM_QUEUE_MANAGER = "queueManager";
    public static final String PARAM_USER = "user";
    public static final String PARAM_PASSWORD = "password";

    public static MetricQueryResult getQmgrMetaResults(String qmgr) {
        MetricQueryResult result = new MetricQueryResult(1L);
        result.setAttribute(PARAM_QUEUE_MANAGER, qmgr);
        return result;
    }
}