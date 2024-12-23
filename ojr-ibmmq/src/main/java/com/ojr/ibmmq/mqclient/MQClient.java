package com.ojr.ibmmq.mqclient;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFMessage;
import com.ibm.mq.headers.pcf.PCFMessageAgent;
import com.ojr.ibmmq.MQDc;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the implementation for connecting and interacting with IBM MQ Queue Manager.
 * It handles operations such as sending and receiving PCF messages.
 */
public class MQClient {
    private static final Logger logger = Logger.getLogger(MQClient.class.getName());

    private final MQDc mqDc;
    private final Hashtable<String, Object> mqProperties;

    private MQQueueManager qmgr;
    private PCFMessageAgent pcfAgent;
    private volatile boolean isConnected = false;

    public MQClient(MQDc mqDc) throws GeneralSecurityException, IOException {
        this.mqDc = mqDc;
        this.mqProperties = createMqProperties();
    }

    public synchronized void connect() throws MQException, MQDataException {
        if (isConnected) {
            return;
        }

        if (mqDc.isLocal()) {
            logger.log(Level.INFO, String.format("Connecting to %s in local binding mode...", mqDc.getQmgr()));
            qmgr = new MQQueueManager(mqDc.getQmgr());
            pcfAgent = new PCFMessageAgent(qmgr);
        } else {
            logger.log(Level.INFO, String.format("Connecting to %s in client mode...", mqDc.getQmgr()));
            qmgr = new MQQueueManager(mqDc.getQmgr(), mqProperties);
            pcfAgent = new PCFMessageAgent(qmgr);
        }

        isConnected = true;
    }

    public void disconnect() {
        try {
            if (pcfAgent != null)
                pcfAgent.disconnect();
        } catch (Exception e) {
            // ignored
        }
        try {
            if (qmgr != null)
                qmgr.close();
        } catch (Exception e) {
            // ignored
        }
        try {
            if (qmgr != null)
                qmgr.disconnect();
        } catch (Exception e) {
            // ignored
        }

        isConnected = false;
    }


    public PCFMessage[] sendPcfMsg(PCFMessage request) throws IOException, MQDataException {
        return pcfAgent.send(request);
    }

    public PCFMessage sendPcfMsg1(PCFMessage request) throws IOException, MQDataException {
        PCFMessage[] responses = pcfAgent.send(request);
        if (responses.length > 0) {
            return responses[0];
        } else {
            return null;
        }
    }

    public MQDc getMqDc() {
        return mqDc;
    }

    public Hashtable<String, Object> getMqProperties() {
        return mqProperties;
    }

    public MQQueueManager getQmgr() {
        return qmgr;
    }

    public PCFMessageAgent getPcfAgent() {
        return pcfAgent;
    }

    private Hashtable<String, Object> createMqProperties() throws GeneralSecurityException, IOException {
        Hashtable<String, Object> props = new Hashtable<>();
        if (StringUtils.isNotBlank(mqDc.getChannel())) {
            props.put(CMQC.CHANNEL_PROPERTY, mqDc.getChannel());
        }
        if (StringUtils.isNotBlank(mqDc.getHost())) {
            props.put(CMQC.HOST_NAME_PROPERTY, mqDc.getHost());
        }
        props.put(CMQC.PORT_PROPERTY, mqDc.getPort());
        if (StringUtils.isNotBlank(mqDc.getUser())) {
            props.put(CMQC.USER_ID_PROPERTY, mqDc.getUser());
        }
        if (StringUtils.isNotBlank(mqDc.getPassword())) {
            props.put(CMQC.PASSWORD_PROPERTY, mqDc.getPassword());
        }

        if (StringUtils.isNotBlank(mqDc.getKeystore()) && StringUtils.isNotBlank(mqDc.getKeystorePassword()) && StringUtils.isNotBlank(mqDc.getCipherSuite())) {
            SSLContext sslContext = getSSLContext(mqDc.getKeystore(), mqDc.getKeystorePassword());
            SSLSocketFactory sf = sslContext.getSocketFactory();
            props.put(MQConstants.SSL_SOCKET_FACTORY_PROPERTY, sf);

            props.put(MQConstants.SSL_CIPHER_SUITE_PROPERTY, mqDc.getCipherSuite());
            props.put(CMQC.TRANSPORT_PROPERTY, CMQC.TRANSPORT_MQSERIES_CLIENT);
        }

        return props;
    }

    /**
     * Initializes and returns an SSLContext configured with the specified keystore file and password.
     *
     * @param keystoreFile     The path to the keystore file containing the necessary certificates and keys.
     * @param keystorePassword The password to access the keystore file.
     * @return An SSLContext configured with the specified keystore.
     * @throws GeneralSecurityException If there is a problem with the security configuration.
     * @throws IOException              If there is an error reading from the keystore file.
     */
    private static SSLContext getSSLContext(String keystoreFile, String keystorePassword) throws GeneralSecurityException, IOException {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream in = Files.newInputStream(Paths.get(keystoreFile))) {
            keystore.load(in, keystorePassword.toCharArray());
        }
        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, keystorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                new SecureRandom());

        return sslContext;
    }
}
