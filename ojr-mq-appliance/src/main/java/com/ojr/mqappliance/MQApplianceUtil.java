package com.ojr.mqappliance;

import com.ojr.core.metric.MetricQueryResult;

import java.util.ArrayList;
import java.util.List;

import static com.ojr.host.HostDcUtil.UNIT_1;

public class MQApplianceUtil {
    public static final String SYSTEM_IBMQMGR_STATUS_NAME = "system.ibmqmgr.status";
    public static final String SYSTEM_IBMQMGR_STATUS_DESC = "Status of ibm mq queue manager";
    public static final String SYSTEM_IBMQMGR_STATUS_UNIT = UNIT_1;

    public static final String APPLIANCE_HOST = "appliance.host";
    public static final String APPLIANCE_USER = "appliance.user";
    public static final String APPLIANCE_PASSWORD = "appliance.password";

    public static List<MetricQueryResult> getApplianceCpuUsageResults(long cpuTime, long cpuTimeIdle) {
        List<MetricQueryResult> results = new ArrayList<>(2);
        MetricQueryResult resultUser = new MetricQueryResult(cpuTime);
        resultUser.setKey("user");
        resultUser.setAttribute("cpu", "cpu");
        resultUser.setAttribute("state", "user");
        results.add(resultUser);
        MetricQueryResult resultIdle = new MetricQueryResult(cpuTimeIdle);
        resultIdle.setKey("idle");
        resultIdle.setAttribute("cpu", "cpu");
        resultIdle.setAttribute("state", "idle");
        results.add(resultIdle);
        return results;
    }

    public static List<MetricQueryResult> getApplianceMemUsageResults(Long usedMem, Long freeMem) {
        List<MetricQueryResult> results = new ArrayList<>(2);
        if (freeMem != null && usedMem != null) {
            MetricQueryResult resultUsed = new MetricQueryResult(usedMem * 1024L * 1024L);
            resultUsed.setKey("used");
            resultUsed.setAttribute("state", "used");
            results.add(resultUsed);
            MetricQueryResult resultFree = new MetricQueryResult(freeMem * 1024L * 1024L);
            resultFree.setKey("free");
            resultFree.setAttribute("state", "free");
            results.add(resultFree);
            return results;
        }
        return null;
    }

    public static List<MetricQueryResult> getApplianceNetworkConnectionsResults(String networkConnections) {
        if (networkConnections != null && !networkConnections.isEmpty()) {
            String[] tokens = networkConnections.split(":");
            if (tokens.length == 11) {
                List<MetricQueryResult> results = new ArrayList<>(11);

                MetricQueryResult established = new MetricQueryResult(Integer.parseInt(tokens[0]));
                MetricQueryResult syn_sent = new MetricQueryResult(Integer.parseInt(tokens[1]));
                MetricQueryResult syn_received = new MetricQueryResult(Integer.parseInt(tokens[2]));
                MetricQueryResult fin_wait_1 = new MetricQueryResult(Integer.parseInt(tokens[3]));
                MetricQueryResult fin_wait_2 = new MetricQueryResult(Integer.parseInt(tokens[4]));
                MetricQueryResult time_wait = new MetricQueryResult(Integer.parseInt(tokens[5]));
                MetricQueryResult closed = new MetricQueryResult(Integer.parseInt(tokens[6]));
                MetricQueryResult closed_wait = new MetricQueryResult(Integer.parseInt(tokens[7]));
                MetricQueryResult last_ack = new MetricQueryResult(Integer.parseInt(tokens[8]));
                MetricQueryResult listen = new MetricQueryResult(Integer.parseInt(tokens[9]));
                MetricQueryResult closing = new MetricQueryResult(Integer.parseInt(tokens[10]));

                established.setKey("ESTABLISHED");
                syn_sent.setKey("SYN_SENT");
                syn_received.setKey("SYN_RECV");
                fin_wait_1.setKey("FIN_WAIT_1");
                fin_wait_2.setKey("FIN_WAIT_2");
                time_wait.setKey("TIME_WAIT");
                closed.setKey("CLOSE");
                closed_wait.setKey("CLOSE_WAIT");
                last_ack.setKey("LAST_ACK");
                listen.setKey("LISTEN");
                closing.setKey("CLOSING");

                established.setAttribute("protocol", "tcp");
                syn_sent.setAttribute("protocol", "tcp");
                syn_received.setAttribute("protocol", "tcp");
                fin_wait_1.setAttribute("protocol", "tcp");
                fin_wait_2.setAttribute("protocol", "tcp");
                time_wait.setAttribute("protocol", "tcp");
                closed.setAttribute("protocol", "tcp");
                closed_wait.setAttribute("protocol", "tcp");
                last_ack.setAttribute("protocol", "tcp");
                listen.setAttribute("protocol", "tcp");
                closing.setAttribute("protocol", "tcp");

                established.setAttribute("state", "ESTABLISHED");
                syn_sent.setAttribute("state", "SYN_SENT");
                syn_received.setAttribute("state", "SYN_RECV");
                fin_wait_1.setAttribute("state", "FIN_WAIT_1");
                fin_wait_2.setAttribute("state", "FIN_WAIT_2");
                time_wait.setAttribute("state", "TIME_WAIT");
                closed.setAttribute("state", "CLOSE");
                closed_wait.setAttribute("state", "CLOSE_WAIT");
                last_ack.setAttribute("state", "LAST_ACK");
                listen.setAttribute("state", "LISTEN");
                closing.setAttribute("state", "CLOSING");

                results.add(established);
                results.add(syn_sent);
                results.add(syn_received);
                results.add(fin_wait_1);
                results.add(fin_wait_2);
                results.add(time_wait);
                results.add(closed);
                results.add(closed_wait);
                results.add(last_ack);
                results.add(listen);
                results.add(closing);

                return results;
            }
        }
        return null;
    }

    public static List<MetricQueryResult> getApplianceNetworkInterfaceResults(String networkInterfaceData) {
        if (networkInterfaceData != null && !networkInterfaceData.isEmpty()) {
            String[] tokens = networkInterfaceData.split(":");
            List<MetricQueryResult> results = new ArrayList<>(tokens.length * 2);
            for (String token : tokens) {
                String[] tokenss = token.split("\\|");
                if (tokenss.length == 3) {
                    MetricQueryResult resultR = new MetricQueryResult(Long.parseLong(tokenss[1]));
                    resultR.setKey(tokenss[0] + ":receive");
                    resultR.setAttribute("device", tokenss[0]);
                    resultR.setAttribute("direction", "receive");
                    results.add(resultR);

                    MetricQueryResult resultT = new MetricQueryResult(Long.parseLong(tokenss[2]));
                    resultT.setKey(tokenss[0] + ":transmit");
                    resultT.setAttribute("device", tokenss[0]);
                    resultT.setAttribute("direction", "transmit");
                    results.add(resultT);
                }
            }
            return results;
        }
        return null;
    }

    public static int convertQmgrStatusToInt(String status) {
        switch (status.toLowerCase()) {
            case "starting":
                return 1;
            case "running":
                return 2;
            case "quiescing":
                return 3;
            case "running as standby":
                return 4;
            case "running elsewhere":
                return 5;
            case "ending immediately":
                return 6;
            case "ending pre-emptively":
                return 7;
            case "ended normally":
                return 8;
            case "ended immediately":
                return 9;
            case "ended unexpectedly":
                return 10;
            case "ended pre-emptively":
                return 11;
            case "status not available":
                return 12;
            default:
                return 0;
        }
    }

    public static List<MetricQueryResult> getQmgrStatusResults(String qmgrStatusData) {
        if (qmgrStatusData != null && !qmgrStatusData.isEmpty()) {
            String[] tokens = qmgrStatusData.split(":");
            List<MetricQueryResult> results = new ArrayList<>(tokens.length);
            for (String token : tokens) {
                String[] tokenss = token.split("\\|");
                if (tokenss.length == 2) {
                    MetricQueryResult result = new MetricQueryResult(convertQmgrStatusToInt(tokenss[1]));
                    result.setKey(tokenss[0]);
                    result.setAttribute("qmgr", tokenss[0]);
                    results.add(result);
                }
            }
            return results;
        }
        return null;
    }
}