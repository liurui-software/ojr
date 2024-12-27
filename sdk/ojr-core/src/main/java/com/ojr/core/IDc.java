package com.ojr.core;

import com.ojr.core.metric.RawMetric;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Predicate;

/**
 * The IDc interface defines the standard methods for a metrics data collector.
 */
public interface IDc<Cfg extends BasicDcConfig> {
    /**
     * Retrieves the resource attributes for the data collection.
     *
     * @return a Resource object containing the attributes.
     */
    Resource retrieveResourceAttributes();

    /**
     * Enriches the resource attributes using the provided ResourceEnricher.
     *
     * @param enricher the ResourceEnricher to use for enriching resource attributes
     */
    void enrichResourceAttributes(ResourceEnricher enricher);

    /**
     * Gets the default SDK meter provider with the specified configuration.
     *
     * @param resource the resource to be used by the meter provider.
     * @return the default SDK meter provider.
     */
    SdkMeterProvider getDefaultSdkMeterProvider(Resource resource);

    /**
     * Returns the default SDK tracer provider configured with the given parameters.
     *
     * @param resource The resource to associate with the tracer provider.
     * @return The default SDK tracer provider.
     */
    SdkTracerProvider getDefaultSdkTraceProvider(Resource resource);

    /**
     * Returns the default SDK logger provider configured with the given parameters.
     *
     * @param resource The resource to associate with the logger provider.
     * @return The default SDK logger provider.
     */
    SdkLoggerProvider getDefaultSdkLogProvider(Resource resource);

    /**
     * Registers metrics with the system.
     */
    void registerMetrics();

    /**
     * Before record a metric.
     * <p>
     * This method is used to prepare or validate the recording of a metric before it is officially recorded.
     * It can be used to perform any necessary checks or preprocessing steps. Return false to bypass the recoding
     *
     * @param rawMetric The metric to be recorded.
     * @return true if the pre-recording is successful, false to bypass the recoding.
     */
    boolean preRecordMetric(RawMetric rawMetric);

    /**
     * Before record a metric with the given name, value, and attributes. Primarily used for Histogram metrics.
     * <p>
     * This method is used to prepare or validate the recording of a metric before it is officially recorded.
     * It can be used to perform any necessary checks or preprocessing steps. Return false to bypass the recoding
     *
     * @param metricName The name of the metric to be recorded.
     * @param value      The value of the metric.
     * @param attributes Additional attributes associated with the metric.
     * @return true if the pre-recording is successful, false to bypass the recoding.
     */
    boolean preRecordMetric(String metricName, Number value,  Map<String, Object> attributes);

    /**
     * Collects data from the data collection system.
     */
    void collectData();

    /**
     * Returns the scheduled executor service used for data collection process.
     *
     * @return a ScheduledExecutorService instance
     */
    ScheduledExecutorService getScheduledExecutorService();

    /**
     * Starts the data collection process.
     */
    void start();

    /**
     * Retrieves a raw metric by its name.
     *
     * @param name the name of the metric to retrieve.
     * @return the RawMetric object with the specified name.
     */
    RawMetric getRawMetric(String name);

    /**
     * Provides an initialized map of raw metrics.
     *
     * @return A map where the key is a string representing the metric name and the value is an instance of RawMetric.
     */
    Map<String, RawMetric> provideInitRawMetricsMap();

    /**
     * Retrieves a map of all raw metrics.
     *
     * @return a map where keys are metric names and values are RawMetric objects.
     */
    Map<String, RawMetric> getRawMetricsMap();

    /**
     * Retrieves a map of all meters.
     *
     * @return a map where keys are meter names and values are Meter objects.
     */
    Map<String, Meter> getMeters();

    /**
     * Initializes the data collector environment.
     *
     * @throws Exception if an error occurs during initialization.
     */
    void initEnv(Map<String, Object> properties, Cfg config) throws Exception;

    /**
     * Reads and sets the built-in configuration parameters from a provided properties map.
     * The method uses default values if certain properties are not present. The default order of execution is: readBuiltinParameters,
     * readExtraParameters, initOnce, processParameters.
     *
     * @param properties A map containing various properties as key-value pairs.
     * @param config     An instance of Cfg containing configuration settings.
     */
    void readBuiltinParameters(Map<String, Object> properties, Cfg config);

    /**
     * Read extra parameters based on the provided properties and configuration. The default order of execution is: readBuiltinParameters,
     * readExtraParameters, initOnce, processParameters.
     *
     * @param properties A map containing various properties as key-value pairs.
     * @param config     An instance of Cfg containing configuration settings.
     */
    void readExtraParameters(Map<String, Object> properties, Cfg config);

    /**
     * Run this initialization just one time for this Data Collector of whatever any number of instances. The default order
     * of execution is: readBuiltinParameters, readExtraParameters, initOnce, processParameters.
     */
    void initOnce() throws Exception;

    /**
     * Process parameters. The default order of execution is: readBuiltinParameters, readExtraParameters, initOnce, processParameters.
     *
     * @param properties A map containing various properties as key-value pairs.
     * @param config     An instance of Cfg containing configuration settings.
     */
    void processParameters(Map<String, Object> properties, Cfg config) throws Exception;

    /**
     * Initializes the OpenTelemetry engine with specified features.
     *
     * @param useMetrics a flag indicating whether to enable metrics collection
     * @param useTraces  a flag indicating whether to enable trace collection
     * @param useLogs    a flag indicating whether to enable log collection
     */
    void initOTelEngine(boolean useMetrics, boolean useTraces, boolean useLogs);

    /**
     * Initializes meters with the provided OpenTelemetry instance.
     *
     * @param openTelemetry the OpenTelemetry instance to use for meter initialization.
     */
    void initMeters(OpenTelemetry openTelemetry);

    /**
     * Returns the filter for resource attributes used when exporting metrics to Prometheus.
     *
     * @param resAttrsFilter The default filter for resource attributes used when exporting metrics to Prometheus.
     * @return The predicate used to filter resource attributes.
     */
    Predicate<String> updateResAttrsFilterForPrometheus(Predicate<String> resAttrsFilter);


    /**
     * Retrieves the URL of the OTel backend.
     *
     * @return The URL of the OTel backend.
     */
    String getBackendUrl();

    /**
     * Sets the URL of the OTel backend.
     *
     * @param backendUrl The URL of the OTel backend to be set.
     */
    void setBackendUrl(String backendUrl);

    /**
     * Retrieves the transport method used for data transmission.
     *
     * @return The transport method.
     */
    String getTransport();

    /**
     * Sets the transport method used for data transmission.
     *
     * @param transport The transport method to be set.
     */
    void setTransport(String transport);

    /**
     * Retrieves the interval at which polls are made.
     *
     * @return The poll interval in milliseconds.
     */
    int getPollInterval();

    /**
     * Sets the interval at which polls are made.
     *
     * @param pollInterval The poll interval in milliseconds to be set.
     */
    void setPollInterval(int pollInterval);

    /**
     * Retrieves the interval at which callbacks are made.
     *
     * @return The callback interval in milliseconds.
     */
    int getCallbackInterval();

    /**
     * Sets the interval at which callbacks are made.
     *
     * @param callbackInterval The callback interval in milliseconds to be set.
     */
    void setCallbackInterval(int callbackInterval);

    /**
     * Retrieves the name of the service.
     *
     * @return The name of the service.
     */
    String getServiceName();

    /**
     * Sets the name of the service.
     *
     * @param serviceName The name of the service to be set.
     */
    void setServiceName(String serviceName);

    /**
     * Retrieves the instance ID of the service.
     *
     * @return The instance ID of the service.
     */
    String getServiceInstanceId();

    /**
     * Sets the instance ID of the service.
     *
     * @param serviceInstanceId The instance ID of the service to be set.
     */
    void setServiceInstanceId(String serviceInstanceId);

    /**
     * Retrieves the timeout for the transport operation.
     *
     * @return The transport timeout in milliseconds.
     */
    long getTransportTimeout();

    /**
     * Sets the timeout for the transport operation.
     *
     * @param transportTimeout The transport timeout in milliseconds to be set.
     */
    void setTransportTimeout(long transportTimeout);

    /**
     * Retrieves the delay before starting the transport operation.
     *
     * @return The transport delay in milliseconds.
     */
    long getTransportDelay();

    /**
     * Sets the delay before starting the transport operation.
     *
     * @param transportDelay The transport delay in milliseconds to be set.
     */
    void setTransportDelay(long transportDelay);

    /**
     * Retrieves the port number for Prometheus metrics exposure.
     *
     * @return The Prometheus port number.
     */
    int getPrometheusPort();

    /**
     * Sets the port number for Prometheus metrics exposure.
     *
     * @param prometheusPort The Prometheus port number to be set.
     */
    void setPrometheusPort(int prometheusPort);

    /**
     * Retrieves the host address for Prometheus metrics exposure.
     *
     * @return The Prometheus host address.
     */
    String getPrometheusHost();

    /**
     * Sets the host address for Prometheus metrics exposure.
     *
     * @param prometheusHost The Prometheus host address to be set.
     */
    void setPrometheusHost(String prometheusHost);

    /**
     * Retrieves the restricted metrics for Prometheus metrics exposure.
     *
     * @return The restricted metrics for Prometheus metrics exposure.
     */
    String[] getPrometheusMetricRestrictions();

    /**
     * Sets the restricted metrics for Prometheus metrics exposure.
     *
     * @param metricRestrictionString The restricted metrics for Prometheus to be set (separated by ",").
     */
    void setPrometheusMetricRestrictions(String metricRestrictionString);

    /**
     * Sets the restricted metrics for Prometheus metrics exposure.
     *
     * @param prometheusMetricRestrictions The restricted metrics for Prometheus to be set.
     */
    void setPrometheusMetricRestrictions(String[] prometheusMetricRestrictions);

    /**
     * Retrieves the restricted metrics
     *
     * @return The restricted metrics
     */
    String[] getMetricRestrictions();

    /**
     * Sets the restricted metrics
     *
     * @param metricRestrictionString The restricted metrics to be set (separated by ",").
     */
    void setMetricRestrictions(String metricRestrictionString);

    /**
     * Sets the restricted metrics.
     *
     * @param metricRestrictions The restricted metrics to be set.
     */
    void setMetricRestrictions(String[] metricRestrictions);

    /**
     * Retrieves the hostname of the current environment.
     *
     * @return the hostname as a String
     */
    String getHostname();

    /**
     * Sets the hostname for the current environment.
     *
     * @param hostname the hostname to be set
     */
    void setHostname(String hostname);

    /**
     * Retrieves the process ID (PID) of the current process.
     *
     * @return the PID as a long
     */
    long getPid();

    /**
     * Sets the process ID (PID) for the current process.
     *
     * @param pid the PID to be set
     */
    void setPid(long pid);

    /**
     * Retrieves the container ID of the current containerized environment.
     *
     * @return the container ID as a String
     */
    String getContainerId();

    /**
     * Sets the container ID for the current containerized environment.
     *
     * @param containerId the container ID to be set
     */
    void setContainerId(String containerId);

}
