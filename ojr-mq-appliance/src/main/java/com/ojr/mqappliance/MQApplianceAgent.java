package com.ojr.mqappliance;

import com.ojr.host.HostDcAgent;
import com.ojr.host.HostDcConfig;

public class MQApplianceAgent extends HostDcAgent<HostDcConfig, MQApplianceDc> {
    public static void main(String[] args) throws Exception {
        MQApplianceAgent agent = new MQApplianceAgent();
        agent.initEnv(HostDcConfig.class, MQApplianceDc.class);
        agent.initOTelEngine();
        agent.start();
    }

}

