package com.ojr.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * The Basic class of Data Collector Agent
 */
public class BasicDcAgent<Cfg extends BasicDcConfig, Dc extends IDc<Cfg>> {
    static{
        if(System.getProperty("java.util.logging.config.file") == null){
            LogManager logManager = LogManager.getLogManager();
            try {
                logManager.readConfiguration(Files.newInputStream(Paths.get("config/logging.properties")));
            } catch (IOException e) {
                System.err.println("Cannot find config/logging.properties!");
                throw new RuntimeException(e);
            }
        }
    }
    private static final Logger logger = Logger.getLogger(BasicDcAgent.class.getName());

    private List<Dc> dcs;

    /**
     * Returns the list of the configured data collector instances.
     *
     * @return a list of Dc<Cfg> representing data collector.
     */
    public List<Dc> getDcs() {
        return dcs;
    }

    /**
     * Reads a YAML configuration file and converts it into an object of the specified class.
     *
     * @param clazz The class type to which the YAML content should be converted.
     * @return An object of type Cfg, representing the deserialized YAML content.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public Cfg readConfigYaml(Class<Cfg> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        String configFile = System.getenv(DcUtil.CONFIG_ENV);
        if (configFile == null) {
            configFile = System.getProperty(DcUtil.CONFIG_ENV);
        }
        if (configFile == null) {
            configFile = DcUtil.CONFIG_YAML;
        }
        return objectMapper.readValue(new File(configFile), clazz);
    }

    /**
     * Processes the global configuration settings.
     *
     * @param cfg The configuration object containing global settings to be processed.
     */
    public void processGlobalConfig(Cfg cfg) {
    }

    /**
     * Initializes the agent with configuration and data collector classes.
     *
     * @param cfgClass The class of the configuration object.
     * @param dcClass The class of the data collector object.
     * @throws Exception If an error occurs during initialization.
     */
    public void initEnv(Class<Cfg> cfgClass, Class<Dc> dcClass) throws Exception {
        Cfg cfg = readConfigYaml(cfgClass);
        processGlobalConfig(cfg);
        List<ConcurrentHashMap<String, Object>> instances = cfg.getInstances();
        dcs = new ArrayList<>(instances.size());
        for (Map<String, Object> props : instances) {
            Dc dc = dcClass.newInstance();
            dcs.add(dc);
            dc.initEnv(props, cfg);
        }
    }

    /**
     * Initializes the OpenTelemetry engine for all data collectors.
     */
    public void initOTelEngine() {
        for (Dc dc : dcs) {
            dc.initOTelEngine(true, false, false);
        }
    }

    /**
     * Starts data collection for all data collectors.
     */
    public void start() {
        int i = 1;
        for (Dc dc : dcs) {
            logger.info("DC No." + i + " is collecting data...");
            dc.start();
            i++;
        }
    }

}
