package com.ojr.ibmmq.mqclient.queriers;

import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.headers.pcf.PCFMessage;
import com.ojr.ibmmq.MQDc;
import com.ojr.ibmmq.mqclient.DataQuerier;
import com.ojr.ibmmq.mqclient.MQClient;
import com.ojr.ibmmq.mqclient.PcfMsgUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ojr.ibmmq.MQDcUtil.*;

public class QmgrQuerier implements DataQuerier {
    private static final Logger logger = Logger.getLogger(QmgrQuerier.class.getName());

    private final MQClient mqClient;

    private final PCFMessage qmgrRequest;
    private final PCFMessage qmgrStatusRequest;
    private final PCFMessage pubsubStatusRequest;

    public QmgrQuerier(MQClient mqClient) {
        this.mqClient = mqClient;

        qmgrRequest = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q_MGR);
        qmgrStatusRequest = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q_MGR_STATUS);
        pubsubStatusRequest = new PCFMessage(CMQCFC.MQCMD_INQUIRE_PUBSUB_STATUS);
    }

    public void handleQmgrRequest() {
        MQDc dc = mqClient.getMqDc();
        try {
            PCFMessage resp = mqClient.sendPcfMsg1(qmgrRequest);

            String version = PcfMsgUtil.getString(resp, CMQC.MQCA_VERSION);
            String platform = PcfMsgUtil.convertPlatform(PcfMsgUtil.getInt(resp, CMQC.MQIA_PLATFORM, -1));
            dc.getRawMetric(QMGR_META).setValue(getQmgrMetadataResult(version, platform));

            dc.getRawMetric(QMGR_CMD_LEVEL).setValue(PcfMsgUtil.getInt(resp, CMQC.MQIA_COMMAND_LEVEL, -1));
            dc.getRawMetric(QMGR_MAX_HANDLES).setValue(PcfMsgUtil.getInt(resp, CMQC.MQIA_MAX_HANDLES, -1));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "handleQmgrRequest() failed!", e);
        }
    }

    public void handleQmgrStatusRequest() {
        MQDc dc = mqClient.getMqDc();
        try {
            PCFMessage resp = mqClient.sendPcfMsg1(qmgrStatusRequest);

            dc.getRawMetric(QMGR_CONNECTION_COUNT).setValue(PcfMsgUtil.getInt(resp, CMQCFC.MQIACF_CONNECTION_COUNT, -1));
            dc.getRawMetric(QMGR_STATUS).setValue(PcfMsgUtil.getInt(resp, CMQCFC.MQIACF_Q_MGR_STATUS, -1));
            dc.getRawMetric(QMGR_CHINIT_STATUS).setValue(PcfMsgUtil.getInt(resp, CMQCFC.MQIACF_CHINIT_STATUS, -1));

            PcfMsgUtil.DateTime dt = PcfMsgUtil.getDateTime(resp, CMQCFC.MQCACF_Q_MGR_START_DATE, CMQCFC.MQCACF_Q_MGR_START_TIME);
            dc.getRawMetric(QMGR_START_TIME).setValue(dt.getEpochSecond());
            dc.getRawMetric(QMGR_START_TIME_FORMATED).setValue(dt.formatAsNumber());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "handleQmgrStatusRequest() failed!", e);
        }
    }

    public void handlePubsubStatusRequest() {
        MQDc dc = mqClient.getMqDc();
        try {
            PCFMessage resp = mqClient.sendPcfMsg1(pubsubStatusRequest);

            dc.getRawMetric(QMGR_PUBSUB_STATUS).setValue(PcfMsgUtil.getInt(resp, CMQCFC.MQIACF_PUBSUB_STATUS, -1));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "handlePubsubStatusRequest() failed!", e);
        }
    }

    public void collectData() {
        handleQmgrRequest();
        handleQmgrStatusRequest();
        handlePubsubStatusRequest();
    }

}
