package com.ojr.linuxhost;

import com.ojr.host.HostDcAgent;
import com.ojr.host.HostDcConfig;

public class LinuxHostAgent extends HostDcAgent<HostDcConfig, LinuxHostDc> {
    public static void main(String[] args) throws Exception {
        LinuxHostAgent agent = new LinuxHostAgent();
        agent.initEnv(HostDcConfig.class, LinuxHostDc.class);
        agent.initOTelEngine();
        agent.start();
    }

}

