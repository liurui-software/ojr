package com.ojr.ibmmq.mqclient.queriers;

import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.headers.pcf.PCFMessage;
import com.ojr.ibmmq.MQDc;
import com.ojr.ibmmq.mqclient.DataQuerier;
import com.ojr.ibmmq.mqclient.MQClient;
import com.ojr.ibmmq.mqclient.PcfMsgUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ojr.ibmmq.MQDcUtil.*;

public class ListenerQuerier implements DataQuerier {
    private static final Logger logger = Logger.getLogger(ListenerQuerier.class.getName());

    private final MQClient mqClient;

    private final PCFMessage inqListenerReq;
    //private final PCFMessage inqListenersStatusReq;

    public ListenerQuerier(MQClient mqClient) {
        this.mqClient = mqClient;

        inqListenerReq = new PCFMessage(CMQCFC.MQCMD_INQUIRE_LISTENER);
        inqListenerReq.addParameter(CMQCFC.MQCACH_LISTENER_NAME, "*");

        //int[] pcfAttrs = new int[] {MQConstants.MQIACF_ALL};
        //inqListenersStatusReq = new PCFMessage(CMQCFC.MQCMD_INQUIRE_LISTENER_STATUS);
        //inqListenersStatusReq.addParameter(CMQCFC.MQCACH_LISTENER_NAME, "*");
        //inqListenersStatusReq.addParameter(MQConstants.MQIACF_LISTENER_STATUS_ATTRS, pcfAttrs);
    }

    @Override
    public void collectData() {
        Set<String> lsnAllNames = handleInqListenerReq();
        handleInqListenerStatusReq(lsnAllNames);
    }

    private Set<String> handleInqListenerReq() {
        Set<String> lsnAllNames = new HashSet<>();
        try {
            PCFMessage[] resps = mqClient.sendPcfMsg(inqListenerReq);
            for (PCFMessage resp : resps) {
                if (resp.getCompCode() != CMQC.MQCC_OK)
                    continue;

                String lsnName = PcfMsgUtil.getString(resp, CMQCFC.MQCACH_LISTENER_NAME);
                if (lsnName != null && !lsnName.isEmpty()) {
                    lsnAllNames.add(lsnName);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "handleInqListenersReq failed!", e);
        }
        return lsnAllNames;
    }

    private void handleInqListenerStatusReq(Set<String> lsnAllNames) {
        MQDc dc = mqClient.getMqDc();

        for (String lsnName : lsnAllNames) {
            try {
                PCFMessage inqListenersStatusReq = new PCFMessage(CMQCFC.MQCMD_INQUIRE_LISTENER_STATUS);
                inqListenersStatusReq.addParameter(CMQCFC.MQCACH_LISTENER_NAME, lsnName);
                PCFMessage resp = mqClient.sendPcfMsg1(inqListenersStatusReq);
                if (resp.getCompCode() != CMQC.MQCC_OK)
                    continue;

                int status = PcfMsgUtil.getInt(resp, CMQCFC.MQIACH_LISTENER_STATUS, -1);
                dc.getRawMetric(LISTENER_STATUS).setValue(getSimpleListenerMetricResult(lsnName, status));

                String ip = PcfMsgUtil.getString(resp, CMQCFC.MQCACH_IP_ADDRESS);
                int port = PcfMsgUtil.getInt(resp, CMQCFC.MQIACH_PORT, -1);
                dc.getRawMetric(LISTENER_META).setValue(getListenerMetadataResult(lsnName, ip, port));

            } catch (Exception e) {
                dc.getRawMetric(LISTENER_STATUS).setValue(getSimpleListenerMetricResult(lsnName, CMQC.MQSVC_STATUS_STOPPED));
            }
        }
    }

}