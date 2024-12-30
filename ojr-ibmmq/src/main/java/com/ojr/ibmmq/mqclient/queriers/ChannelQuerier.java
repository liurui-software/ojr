package com.ojr.ibmmq.mqclient.queriers;

import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.constants.CMQXC;
import com.ibm.mq.headers.pcf.PCFMessage;
import com.ojr.ibmmq.MQDc;
import com.ojr.ibmmq.mqclient.DataQuerier;
import com.ojr.ibmmq.mqclient.MQClient;
import com.ojr.ibmmq.mqclient.PcfMsgUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ojr.ibmmq.MQDcUtil.*;

public class ChannelQuerier implements DataQuerier {
    private static final Logger logger = Logger.getLogger(ChannelQuerier.class.getName());

    private final MQClient mqClient;

    private final PCFMessage inqChannelStatusReq;

    public ChannelQuerier(MQClient mqClient) {
        this.mqClient = mqClient;

        inqChannelStatusReq = new PCFMessage(CMQCFC.MQCMD_INQUIRE_CHANNEL_STATUS);
        inqChannelStatusReq.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, "*");
        inqChannelStatusReq.addParameter(CMQCFC.MQIACH_CHANNEL_TYPE, CMQXC.MQCHT_ALL);
    }

    @Override
    public void collectData() {
        MQDc dc = mqClient.getMqDc();
        try {
            PCFMessage[] resps = mqClient.sendPcfMsg(inqChannelStatusReq);
            for (PCFMessage resp : resps) {
                if (resp.getCompCode() != CMQC.MQCC_OK)
                    continue;

                String chnName = PcfMsgUtil.getString(resp, CMQCFC.MQCACH_CHANNEL_NAME);

                int status = PcfMsgUtil.getInt(resp, CMQCFC.MQIACH_CHANNEL_STATUS, -1);
                dc.getRawMetric(CHANNEL_STATUS).setValue(getSimpleChannelMetricResult(chnName, status));

                int indoubtStatus = PcfMsgUtil.getInt(resp, CMQCFC.MQIACH_INDOUBT_STATUS, -1);
                dc.getRawMetric(CHANNEL_INDOUBT_STATUS).setValue(getSimpleChannelMetricResult(chnName, indoubtStatus));

                int buffersSent = PcfMsgUtil.getInt(resp, CMQCFC.MQIACH_BUFFERS_SENT, -1);
                int buffersReceived = PcfMsgUtil.getInt(resp, CMQCFC.MQIACH_BUFFERS_RECEIVED, -1);
                dc.getRawMetric(CHANNEL_BUFFERS_SENT).setValue(getSimpleChannelMetricResult(chnName, buffersSent));
                dc.getRawMetric(CHANNEL_BUFFERS_RECEIVED).setValue(getSimpleChannelMetricResult(chnName, buffersReceived));

                int bytesSent = PcfMsgUtil.getInt(resp, CMQCFC.MQIACH_BYTES_SENT, -1);
                int bytesReceived = PcfMsgUtil.getInt(resp, CMQCFC.MQIACH_BYTES_RECEIVED, -1);
                dc.getRawMetric(CHANNEL_BYTES_SENT).setValue(getSimpleChannelMetricResult(chnName, bytesSent));
                dc.getRawMetric(CHANNEL_BYTES_RECEIVED).setValue(getSimpleChannelMetricResult(chnName, bytesReceived));

                collectChannelConfig(chnName);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "handleInqChannelsReq failed! " + e.getMessage());
        }
    }

    private void collectChannelConfig(String chnName) {
        MQDc dc = mqClient.getMqDc();
        PCFMessage inqChannelReq = new PCFMessage(CMQCFC.MQCMD_INQUIRE_CHANNEL);
        inqChannelReq.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, chnName);
        inqChannelReq.addParameter(CMQCFC.MQIACH_CHANNEL_TYPE, CMQXC.MQCHT_ALL);

        try {
            PCFMessage[] resps = mqClient.sendPcfMsg(inqChannelReq);
            for (PCFMessage resp : resps) {
                if (resp.getCompCode() != CMQC.MQCC_OK)
                    continue;

                int type = PcfMsgUtil.getInt(resp, CMQCFC.MQIACH_CHANNEL_TYPE, -1);
                dc.getRawMetric(CHANNEL_TYPE).setValue(getSimpleChannelMetricResult(chnName, type));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "collectChannelConfig failed!", e);
        }
    }

}