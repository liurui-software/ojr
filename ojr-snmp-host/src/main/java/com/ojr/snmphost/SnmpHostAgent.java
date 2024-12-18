package com.ojr.snmphost;

import com.ojr.host.HostDcAgent;
import com.ojr.host.HostDcConfig;

public class SnmpHostAgent extends HostDcAgent<HostDcConfig, SnmpHostDc> {
    public static void main(String[] args) throws Exception {
        SnmpHostAgent agent = new SnmpHostAgent();
        agent.initEnv(HostDcConfig.class, SnmpHostDc.class);
        agent.initOTelEngine();
        agent.start();
    }

}

