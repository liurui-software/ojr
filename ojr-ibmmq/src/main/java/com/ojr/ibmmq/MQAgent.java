package com.ojr.ibmmq;

import com.ojr.core.BasicDcAgent;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.LogManager;

/**
 * Represents an OTel agent (Receiver + OTLP/Prometheus exporter) for IBM MQ.
 * Extends the BasicDcAgent class, which provides generic functionality for data collection agents.
 * Utilizes MQDcConfig for configuration and MQDc for data collection.
 */
public class MQAgent extends BasicDcAgent<MQDcConfig, MQDc> {
    public static void main(String[] args) throws Exception {
        MQAgent agent = new MQAgent();
        agent.initEnv(MQDcConfig.class, MQDc.class);
        agent.initOTelEngine();
        agent.start();
    }
}

