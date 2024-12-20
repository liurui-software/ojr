package com.ojr.ibmmq;

import com.ojr.core.BasicDcAgent;

public class MQAgent extends BasicDcAgent<MQDcConfig, MQDc> {
    public static void main(String[] args) throws Exception {
        MQAgent agent = new MQAgent();
        agent.initEnv(MQDcConfig.class, MQDc.class);
        agent.initOTelEngine();
        agent.start();
    }

}

