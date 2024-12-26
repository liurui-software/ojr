package com.ojr.core;

import com.ojr.core.metric.RawMetric;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.*;
import io.opentelemetry.sdk.resources.Resource;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for Data Collector operations.
 */
public class DcUtil {
    private static final Logger logger = Logger.getLogger(DcUtil.class.getName());

    public final static String OCR_VERSION = "0.5.0";
    public static final String DEFAULT = "default"; // Default key for data points
    public static final String N_A = "N/A";

    /* Configuration constants for the Data Collector:
     */
    public final static String OTEL_POLLING_INTERVAL = "otel.poll.interval"; // Polling interval in seconds
    public static final int DEFAULT_OTEL_POLL_INTERVAL = 25;  // Default polling interval in seconds
    public final static String OTEL_CALLBACK_INTERVAL = "otel.callback.interval"; // Callback interval in seconds
    public static final int DEFAULT_OTEL_CALLBACK_INTERVAL = 30; // Default callback interval in seconds

    public final static String OTEL_BACKEND_URL = "otel.backend.url"; // URL of the OpenTelemetry backend
    public final static String DEFAULT_OTEL_BACKEND_URL = "http://127.0.0.1:4318"; // Default OpenTelemetry backend URL
    public final static String OTEL_TRANSPORT = "otel.transport"; // The transport protocol for the OpenTelemetry exporter
    public final static String DEFAULT_OTEL_TRANSPORT = "http"; // otlp/http
    public final static String GRPC = "grpc";
    public final static String HTTP = "http";
    public final static String PROMETHEUS = "prometheus";

    public static final String PROMETHEUS_PORT = "prometheus.port";
    public static final int DEFAULT_PROMETHEUS_PORT = 16543; // Default Prometheus port
    public static final String PROMETHEUS_HOST = "prometheus.host";
    public static final String DEFAULT_PROMETHEUS_HOST = "0.0.0.0";
    public static final String PROMETHEUS_RESTRICTED_METRICS = "prometheus.restricted.metrics";

    public final static String OTEL_SERVICE_NAME = "otel.service.name"; // Service name for OpenTelemetry
    public final static String DEFAULT_OTEL_SERVICE_NAME = "OJR"; // Default service name
    public final static String OTEL_SERVICE_INSTANCE_ID = "otel.service.instance.id"; // Instance ID for OpenTelemetry service

    public static final String OTEL_TRANSPORT_TIMEOUT = "otel.transport.timeout"; // transport timeout in milliseconds
    public static final long DEFAULT_OTEL_TRANSPORT_TIMEOUT = 10000L; // transport timeout in milliseconds
    public static final String OTEL_TRANSPORT_DELAY = "otel.transport.delay"; // transport delay (used for Batch processor) in milliseconds
    public static final long DEFAULT_OTEL_TRANSPORT_DELAY = 100L; // transport delay (used for Batch processor) in milliseconds

    // Standard environment variables
    public static final String OTEL_RESOURCE_ATTRIBUTES = "OTEL_RESOURCE_ATTRIBUTES"; // Resource attributes for OpenTelemetry
    public static final String OTEL_EXPORTER_OTLP_HEADERS = "OTEL_EXPORTER_OTLP_HEADERS"; // Headers for OTLP exporter
    public static final String OTEL_EXPORTER_OTLP_CERTIFICATE = "OTEL_EXPORTER_OTLP_CERTIFICATE"; // Certificate for OTLP exporter

    // Configuration files
    public static final String OJR_PLUGIN = "ojr"; // Plugin name

    /**
     * Merges resource attributes from environment variables into the given resource.
     *
     * @param resource The initial resource to merge with.
     * @return The merged resource.
     */
    public static Resource mergeResourceAttributesFromEnv(Resource resource) {
        String resAttrs = System.getenv(OTEL_RESOURCE_ATTRIBUTES);
        if (resAttrs == null) {
            resAttrs = System.getProperty(OTEL_RESOURCE_ATTRIBUTES);
        }
        if (resAttrs != null) {
            for (String resAttr : resAttrs.split(",")) {
                String[] kv = resAttr.split("=");
                if (kv.length != 2)
                    continue;
                String key = kv[0].trim();
                String value = kv[1].trim();
                resource = resource.merge(Resource.create(Attributes.of(AttributeKey.stringKey(key), value)));
            }
        }
        return resource;
    }

    /**
     * Retrieves headers from environment variables.
     *
     * @return A map of headers.
     */
    public static Map<String, String> getHeadersFromEnv() {
        String resAttrs = System.getenv(OTEL_EXPORTER_OTLP_HEADERS);
        if (resAttrs == null) {
            resAttrs = System.getProperty(OTEL_EXPORTER_OTLP_HEADERS);
        }
        if (resAttrs == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        for (String resAttr : resAttrs.split(",")) {
            String[] kv = resAttr.split("=");
            if (kv.length != 2)
                continue;
            String key = kv[0].trim();
            String value = kv[1].trim();
            map.put(key, value);
        }
        return map;
    }

    /**
     * Reads and returns the certificate from the specified file path.
     *
     * @return The certificate as a byte array.
     */
    public static byte[] getCert() {
        String certFile = System.getenv(OTEL_EXPORTER_OTLP_CERTIFICATE);
        if (certFile == null) {
            certFile = System.getProperty(OTEL_EXPORTER_OTLP_CERTIFICATE);
        }
        if (certFile != null) {
            try {
                return Files.readAllBytes(new File(certFile).toPath());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Certification file is invalid: {0}", certFile);
            }
        }
        return null;
    }

    /**
     * Converts a map of key-value pairs to OpenTelemetry attributes.
     *
     * @param map The map to convert.
     * @return OpenTelemetry attributes.
     */
    public static Attributes convertMapToAttributes(Map<String, Object> map) {
        AttributesBuilder builder = Attributes.builder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Long) {
                builder.put(key, (Long) value);
            } else if (value instanceof Double) {
                builder.put(key, (Double) value);
            } else if (value instanceof Boolean) {
                builder.put(key, (Boolean) value);
            } else {
                builder.put(key, value.toString());
            }
        }
        return builder.build();
    }

    /**
     * Registers a metric with the given meter.
     *
     * @param meters    The map of meters.
     * @param rawMetric The raw metric to register.
     */
    public static void registerMetric(Map<String, Meter> meters, RawMetric rawMetric) {
        Consumer<ObservableLongMeasurement> recordLongMetric = measurement -> {
            rawMetric.purgeOutdatedDps();
            boolean clearDps = rawMetric.isClearDps();
            Iterator<Map.Entry<String, RawMetric.DataPoint>> iterator = rawMetric.getDataPoints().entrySet().iterator();
            while (iterator.hasNext()) {
                RawMetric.DataPoint dp = iterator.next().getValue();
                Long value = dp.getLongValue();
                if (value == null)
                    continue;
                measurement.record(value, convertMapToAttributes(dp.getAttributes()));
                if (clearDps) {
                    iterator.remove();
                }
            }
        };
        Consumer<ObservableDoubleMeasurement> recordDoubleMetric = measurement -> {
            rawMetric.purgeOutdatedDps();
            boolean clearDps = rawMetric.isClearDps();
            Iterator<Map.Entry<String, RawMetric.DataPoint>> iterator = rawMetric.getDataPoints().entrySet().iterator();
            while (iterator.hasNext()) {
                RawMetric.DataPoint dp = iterator.next().getValue();
                Double value = dp.getDoubleValue();
                if (value == null)
                    continue;
                measurement.record(value, convertMapToAttributes(dp.getAttributes()));
                if (clearDps) {
                    iterator.remove();
                }
            }
        };

        Meter meter = meters.get(rawMetric.getMeterName());
        switch (rawMetric.getInstrumentType()) {
            case GAUGE:
                if (rawMetric.isInteger())
                    meter.gaugeBuilder(rawMetric.getName()).ofLongs().setUnit(rawMetric.getUnit()).setDescription(rawMetric.getDescription())
                            .buildWithCallback(recordLongMetric);
                else
                    meter.gaugeBuilder(rawMetric.getName()).setUnit(rawMetric.getUnit()).setDescription(rawMetric.getDescription())
                            .buildWithCallback(recordDoubleMetric);
                break;
            case COUNTER:
                if (rawMetric.isInteger())
                    meter.counterBuilder(rawMetric.getName()).setUnit(rawMetric.getUnit()).setDescription(rawMetric.getDescription())
                            .buildWithCallback(recordLongMetric);
                else
                    meter.counterBuilder(rawMetric.getName()).ofDoubles().setUnit(rawMetric.getUnit()).setDescription(rawMetric.getDescription())
                            .buildWithCallback(recordDoubleMetric);
                break;
            case UPDOWN_COUNTER:
                if (rawMetric.isInteger())
                    meter.upDownCounterBuilder(rawMetric.getName()).setUnit(rawMetric.getUnit()).setDescription(rawMetric.getDescription())
                            .buildWithCallback(recordLongMetric);
                else
                    meter.upDownCounterBuilder(rawMetric.getName()).ofDoubles().setUnit(rawMetric.getUnit()).setDescription(rawMetric.getDescription())
                            .buildWithCallback(recordDoubleMetric);
                break;
            case HISTOGRAM:
                if (rawMetric.isInteger()) {
                    LongHistogramBuilder builder = meter.histogramBuilder(rawMetric.getName()).setUnit(rawMetric.getUnit()).setDescription(rawMetric.getDescription())
                            .ofLongs();
                    List<Long> boundaries = rawMetric.getLongBucketBoundaries();
                    if (boundaries != null)
                        builder.setExplicitBucketBoundariesAdvice(boundaries);
                    rawMetric.setLongHistogram(builder.build());
                } else {
                    DoubleHistogramBuilder builder = meter.histogramBuilder(rawMetric.getName()).setUnit(rawMetric.getUnit()).setDescription(rawMetric.getDescription());
                    List<Double> boundaries = rawMetric.getDoubleBucketBoundaries();
                    if (boundaries != null)
                        builder.setExplicitBucketBoundariesAdvice(boundaries);
                    rawMetric.setDoubleHistogram(builder.build());
                }
            default:
                logger.log(Level.WARNING, "Currently only following instrument types are supported, Gauge, Counter, UpDownCounter, while your type is {0}", rawMetric.getInstrumentType());
        }
    }

    /**
     * Retrieves the process ID (PID) of the current JVM.
     *
     * @return The PID or -1 if it cannot be determined.
     */
    public static long getPid() {
        // While this is not strictly defined, almost all commonly used JVMs format this as
        // pid@hostname.
        String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
        int atIndex = runtimeName.indexOf('@');
        if (atIndex >= 0) {
            String pidString = runtimeName.substring(0, atIndex);
            try {
                return Long.parseLong(pidString);
            } catch (NumberFormatException ignored) {
                // Ignore parse failure.
            }
        }
        return -1;
    }

    /**
     * Base64 decodes the given string.
     *
     * @param encodedStr The encoded string.
     * @return The decoded string.
     */
    public static String base64Decode(String encodedStr) {
        if (encodedStr == null)
            return null;
        return new String(Base64.getDecoder().decode(encodedStr));
    }

}
