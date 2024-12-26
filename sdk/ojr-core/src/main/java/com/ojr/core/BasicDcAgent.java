package com.ojr.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    // Configuration files & paths
    public static final String DEFAULT_LOGGING_FILE = "logging.properties"; // Default value of Logging configuration file
    public static final String DEFAULT_CONFIG_FILE = "config.yaml"; // Default value of configuration file
    public static final String ENV_CONFIG_PATH = "OJR_CONFIG"; // Environment variable for configuration file path
    public static final String ENV_OJR_DIR = "OJR_DIR"; // Environment variable for configuration file directory
    public static final String DEFAULT_OJR_DIR = "config"; // Default value of configuration file directory

    public static final String OJR_DIR = getEnvProperty(ENV_OJR_DIR, DEFAULT_OJR_DIR);

    static {
        if (System.getProperty("java.util.logging.config.file") == null) {
            LogManager logManager = LogManager.getLogManager();
            File loggingFile = new File(getFullPath(OJR_DIR, DEFAULT_LOGGING_FILE));
            try {
                if (loggingFile.exists()) {
                    logManager.readConfiguration(Files.newInputStream(loggingFile.toPath()));
                }
            } catch (IOException e) {
                System.err.println("Cannot open logging config file: " + loggingFile + "!");
            }
        }
    }

    public static String getEnvProperty(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null) {
            value = System.getProperty(key);
        }
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    public static String getFullPath(String path, String file) {
        if (path == null)
            return file;

        if (path.endsWith(File.separator))
            return path + file;

        return path + File.separator + file;
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
        String configFile = getEnvProperty(ENV_CONFIG_PATH, getFullPath(OJR_DIR, DEFAULT_CONFIG_FILE));
        return new ObjectMapper(new YAMLFactory()).readValue(new File(configFile), clazz);
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
     * @param dcClass  The class of the data collector object.
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
