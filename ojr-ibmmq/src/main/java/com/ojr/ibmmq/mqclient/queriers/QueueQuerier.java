package com.ojr.ibmmq.mqclient.queriers;

import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.headers.pcf.PCFMessage;
import com.ojr.ibmmq.MQDc;
import com.ojr.ibmmq.mqclient.DataQuerier;
import com.ojr.ibmmq.mqclient.MQClient;
import com.ojr.ibmmq.mqclient.PcfMsgUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ojr.ibmmq.MQDcUtil.*;

public class QueueQuerier implements DataQuerier {
    private static final Logger logger = Logger.getLogger(QueueQuerier.class.getName());

    private final MQClient mqClient;
    private final List<String> namePatterns;

    public QueueQuerier(MQClient mqClient, String queuesMonitored) {
        this.mqClient = mqClient;
        String[] names = queuesMonitored.split("\\|");
        namePatterns = new ArrayList<>();
        for (String name : names) {
            String name1 = name.trim();
            if (!name1.isEmpty()) {
                namePatterns.add(name1);
            }
        }
    }

    public void collectData() {
        for (String namePattern : namePatterns) {
            this.collectData(namePattern);
        }
    }

    public void collectData(String namePattern) {
        handleQueuesRequest(namePattern);
        handleQueuesStatusRequest(namePattern);
        handleQueuesResetStatsRequest(namePattern);
    }

    private void handleQueuesRequest(String namePattern) {
        MQDc dc = mqClient.getMqDc();
        PCFMessage request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q);
        request.addParameter(CMQC.MQCA_Q_NAME, namePattern);
        try {
            PCFMessage[] resps = mqClient.sendPcfMsg(request);
            for (PCFMessage resp : resps) {
                if (resp.getCompCode() != CMQC.MQCC_OK)
                    continue;

                String qName = PcfMsgUtil.getString(resp, CMQC.MQCA_Q_NAME);
                String qType = PcfMsgUtil.getQueueType(PcfMsgUtil.getInt(resp, CMQC.MQIA_Q_TYPE, -1));
                String qDelivery = PcfMsgUtil.getQueueDelivery(PcfMsgUtil.getInt(resp, CMQC.MQIA_MSG_DELIVERY_SEQUENCE, -1));
                String qUsage = PcfMsgUtil.getQueueUsage(PcfMsgUtil.getInt(resp, CMQC.MQIA_USAGE, -1));
                dc.getRawMetric(QUEUE_META).setValue(getQueueMetadataResult(qName, qType, qDelivery, qUsage));

                dc.getRawMetric(QUEUE_DEPTH).setValue(getSimpleQueueMetricResult(qName, PcfMsgUtil.getInt(resp, CMQC.MQIA_CURRENT_Q_DEPTH, -1)));
                dc.getRawMetric(QUEUE_MAX_DEPTH).setValue(getSimpleQueueMetricResult(qName, PcfMsgUtil.getInt(resp, CMQC.MQIA_MAX_Q_DEPTH, -1)));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "handleQueuesRequest() failed!", e);
        }
    }

    private void handleQueuesStatusRequest(String namePattern) {
        MQDc dc = mqClient.getMqDc();
        PCFMessage request = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q_STATUS);
        request.addParameter(CMQC.MQCA_Q_NAME, namePattern);
        try {
            PCFMessage[] resps = mqClient.sendPcfMsg(request);
            for (PCFMessage resp : resps) {
                if (resp.getCompCode() != CMQC.MQCC_OK)
                    continue;

                String qName = PcfMsgUtil.getString(resp, CMQC.MQCA_Q_NAME);
                dc.getRawMetric(QUEUE_UNCOMMITED_MSG).setValue(getSimpleQueueMetricResult(qName, PcfMsgUtil.getInt(resp, CMQCFC.MQIACF_UNCOMMITTED_MSGS, -1)));
                dc.getRawMetric(QUEUE_OPEN_INPUT).setValue(getSimpleQueueMetricResult(qName, PcfMsgUtil.getInt(resp, CMQC.MQIA_OPEN_INPUT_COUNT, -1)));
                dc.getRawMetric(QUEUE_OPEN_OUTPUT).setValue(getSimpleQueueMetricResult(qName, PcfMsgUtil.getInt(resp, CMQC.MQIA_OPEN_OUTPUT_COUNT, -1)));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "handleQueuesStatusRequest() failed!", e);
        }
    }

    private void handleQueuesResetStatsRequest(String namePattern) {
        MQDc dc = mqClient.getMqDc();
        PCFMessage request = new PCFMessage(CMQCFC.MQCMD_RESET_Q_STATS);
        request.addParameter(CMQC.MQCA_Q_NAME, namePattern);
        try {
            PCFMessage[] resps = mqClient.sendPcfMsg(request);
            for (PCFMessage resp : resps) {
                if (resp.getCompCode() != CMQC.MQCC_OK)
                    continue;

                String qName = PcfMsgUtil.getString(resp, CMQC.MQCA_Q_NAME);
                dc.getRawMetric(QUEUE_ENQ_COUNT).setValue(getSimpleQueueMetricResult(qName, PcfMsgUtil.getInt(resp, CMQC.MQIA_MSG_ENQ_COUNT, -1)));
                dc.getRawMetric(QUEUE_DEQ_COUNT).setValue(getSimpleQueueMetricResult(qName, PcfMsgUtil.getInt(resp, CMQC.MQIA_MSG_DEQ_COUNT, -1)));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "handleQueuesResetStatsRequest() failed!", e);
        }
    }

}
