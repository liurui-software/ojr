package com.ojr.core;

import com.ojr.core.metric.OjrPrometheusHttpServer;
import com.ojr.core.metric.RawMetric;
import com.ojr.core.resources.ContainerResource;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter;
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporterBuilder;
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter;
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporterBuilder;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporterBuilder;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporterBuilder;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporterBuilder;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporterBuilder;
import io.opentelemetry.exporter.prometheus.PrometheusMetricReader;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.OpenTelemetrySdkBuilder;
import io.opentelemetry.sdk.common.export.MemoryMode;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.semconv.ServiceAttributes;
import io.opentelemetry.semconv.TelemetryAttributes;
import io.opentelemetry.semconv.incubating.ContainerIncubatingAttributes;
import io.opentelemetry.semconv.incubating.HostIncubatingAttributes;
import io.opentelemetry.semconv.incubating.ProcessIncubatingAttributes;
import io.opentelemetry.semconv.incubating.ServiceIncubatingAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import static com.ojr.core.DcUtil.getCert;

/**
 * Abstract base class for Data Collectors (DCs) that provides common functionality.
 */
public abstract class AbstractDc<Cfg extends BasicDcConfig> implements IDc<Cfg> {
    // Thread-safe map to store meters
    private final Map<String, Meter> meters = new ConcurrentHashMap<>();
    // Map to store raw metrics
    private final Map<String, RawMetric> rawMetricsMap = new ConcurrentHashMap<>();

    private int pollInterval = DcUtil.DEFAULT_OTEL_POLL_INTERVAL;
    private int callbackInterval = DcUtil.DEFAULT_OTEL_CALLBACK_INTERVAL;

    private String backendUrl = DcUtil.DEFAULT_OTEL_BACKEND_URL;
    private String transport = DcUtil.DEFAULT_OTEL_TRANSPORT;

    private int prometheusPort = DcUtil.DEFAULT_PROMETHEUS_PORT;
    private String prometheusHost = DcUtil.DEFAULT_PROMETHEUS_HOST;
    private String[] prometheusMetricRestrictions = null;

    private String[] metricRestrictions = null;

    private String serviceName = DcUtil.DEFAULT_OTEL_SERVICE_NAME;
    private String serviceInstanceId = null;

    private long transportTimeout = DcUtil.DEFAULT_OTEL_TRANSPORT_TIMEOUT;
    private long transportDelay = DcUtil.DEFAULT_OTEL_TRANSPORT_DELAY;

    private String hostname = null;
    private Long pid = null;
    private String containerId = null;

    private static final String METRICS_SUFFIX = "/v1/metrics";
    private static final String TRACES_SUFFIX = "/v1/traces";
    private static final String LOGS_SUFFIX = "/v1/logs";

    @Override
    public String getBackendUrl() {
        return backendUrl;
    }

    @Override
    public void setBackendUrl(String backendUrl) {
        if (backendUrl == null) {
            this.backendUrl = DcUtil.DEFAULT_OTEL_BACKEND_URL;
        } else {
            if (backendUrl.endsWith(METRICS_SUFFIX))
                this.backendUrl = backendUrl.substring(0, backendUrl.length() - METRICS_SUFFIX.length());
            else if (backendUrl.endsWith(TRACES_SUFFIX))
                this.backendUrl = backendUrl.substring(0, backendUrl.length() - TRACES_SUFFIX.length());
            else if (backendUrl.endsWith(LOGS_SUFFIX))
                this.backendUrl = backendUrl.substring(0, backendUrl.length() - LOGS_SUFFIX.length());
            else this.backendUrl = backendUrl;
        }
    }

    @Override
    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        if (transport != null) this.transport = transport.toLowerCase();
    }

    @Override
    public int getPollInterval() {
        return pollInterval;
    }

    @Override
    public void setPollInterval(int pollInterval) {
        this.pollInterval = pollInterval;
    }

    @Override
    public int getCallbackInterval() {
        return callbackInterval;
    }

    @Override
    public void setCallbackInterval(int callbackInterval) {
        this.callbackInterval = callbackInterval;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    @Override
    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    @Override
    public long getTransportTimeout() {
        return transportTimeout;
    }

    @Override
    public void setTransportTimeout(long transportTimeout) {
        this.transportTimeout = transportTimeout;
    }

    @Override
    public long getTransportDelay() {
        return transportDelay;
    }

    @Override
    public void setTransportDelay(long transportDelay) {
        this.transportDelay = transportDelay;
    }

    @Override
    public int getPrometheusPort() {
        return prometheusPort;
    }

    @Override
    public void setPrometheusPort(int prometheusPort) {
        this.prometheusPort = prometheusPort;
    }

    @Override
    public String getPrometheusHost() {
        return prometheusHost;
    }

    @Override
    public void setPrometheusHost(String prometheusHost) {
        this.prometheusHost = prometheusHost;
    }

    @Override
    public String[] getPrometheusMetricRestrictions() {
        return prometheusMetricRestrictions;
    }

    private String[] parseMetricRestrictions(String metricRestrictionString) {
        String[] restrictions = null;

        if (metricRestrictionString != null) {
            String[] restrictions0 = metricRestrictionString.split(",");
            restrictions = new String[restrictions0.length];
            for (int i = 0; i < restrictions0.length; i++) {
                restrictions[i] = restrictions0[i].trim();
            }

        }
        return restrictions;
    }

    @Override
    public void setPrometheusMetricRestrictions(String metricRestrictionString) {
        prometheusMetricRestrictions = parseMetricRestrictions(metricRestrictionString);
    }

    @Override
    public void setPrometheusMetricRestrictions(String[] prometricsMetricRestrictions) {
        this.prometheusMetricRestrictions = prometricsMetricRestrictions;
    }

    @Override
    public String[] getMetricRestrictions() {
        return metricRestrictions;
    }

    @Override
    public void setMetricRestrictions(String metricRestrictionString) {
        metricRestrictions = parseMetricRestrictions(metricRestrictionString);
    }

    @Override
    public void setMetricRestrictions(String[] MetricRestrictions) {
        this.metricRestrictions = MetricRestrictions;
    }

    @Override
    public String getHostname() {
        if (hostname != null) return hostname;

        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = DcUtil.N_A;
        }

        return hostname;
    }

    @Override
    public void setHostname(String hostname) {
        if (hostname == null) {
            this.hostname = DcUtil.N_A;
        }
        this.hostname = hostname;
    }

    @Override
    public long getPid() {
        if (pid != null) return pid;

        pid = DcUtil.getPid();
        return pid;
    }

    @Override
    public void setPid(long pid) {
        this.pid = pid;
    }

    @Override
    public String getContainerId() {
        if (containerId != null) return containerId;

        containerId = new ContainerResource().getContainerId().orElse(DcUtil.N_A);
        return containerId;
    }

    @Override
    public void setContainerId(String containerId) {
        if (containerId == null) {
            this.containerId = DcUtil.N_A;
        }
        this.containerId = containerId;
    }

    /**
     * Returns the map of meters.
     *
     * @return Map of meters
     */
    @Override
    public Map<String, Meter> getMeters() {
        return meters;
    }

    /**
     * Registers all raw metrics with the meters.
     */
    @Override
    public void registerMetrics() {
        // Iterate through the raw metrics and register each one
        for (RawMetric rawMetric : rawMetricsMap.values()) {
            DcUtil.registerMetric(meters, rawMetric, this);
        }
    }

    @Override
    public boolean preRecordMetric(RawMetric rawMetric) {
        if (metricRestrictions == null) return true;

        return !Arrays.asList(metricRestrictions).contains(rawMetric.getName());
    }

    @Override
    public boolean preRecordMetric(String metricName, Number value, Map<String, Object> attributes) {
        if (metricRestrictions == null) return true;

        return !Arrays.asList(metricRestrictions).contains(metricName);
    }

    /**
     * Retrieves a raw metric by its name.
     *
     * @param name Name of the raw metric
     * @return RawMetric object or null if not found
     */
    @Override
    public RawMetric getRawMetric(String name) {
        return rawMetricsMap.get(name);
    }

    /**
     * Returns the map of raw metrics.
     *
     * @return Map of raw metrics
     */
    @Override
    public Map<String, RawMetric> getRawMetricsMap() {
        return rawMetricsMap;
    }

    /**
     * Creates an OTLP gRPC metric exporter with the specified configuration.
     *
     * @param headers Additional headers to include
     * @param cert    Trusted certificates
     * @return OTLP gRPC metric exporter
     */
    public MetricExporter createOtlpGrpcMetricExporter(Map<String, String> headers, byte[] cert) {
        OtlpGrpcMetricExporterBuilder builder = OtlpGrpcMetricExporter.builder().setEndpoint(backendUrl).setTimeout(transportTimeout, TimeUnit.MILLISECONDS);

        HeadersSupplier supplier = HeadersSupplier.INSTANCE;
        builder.setHeaders(supplier::getHeaders);
        if (headers != null) {
            supplier.updateHeaders(headers);
        }
        if (cert != null) {
            builder.setTrustedCertificates(cert);
        }

        return builder.build();
    }

    /**
     * Creates an OTLP gRPC SpanExporter with the specified configuration.
     *
     * @param headers Additional headers to be included in the gRPC requests.
     * @param cert    Trusted certificates for the gRPC connection.
     * @return A configured SpanExporter instance.
     */
    public SpanExporter createOtlpGrpcTraceExporter(Map<String, String> headers, byte[] cert) {
        OtlpGrpcSpanExporterBuilder builder = OtlpGrpcSpanExporter.builder().setEndpoint(backendUrl).setTimeout(transportTimeout, TimeUnit.MILLISECONDS);

        HeadersSupplier supplier = HeadersSupplier.INSTANCE;
        builder.setHeaders(supplier::getHeaders);
        if (headers != null) {
            supplier.updateHeaders(headers);
        }
        if (cert != null) {
            builder.setTrustedCertificates(cert);
        }

        return builder.build();
    }

    /**
     * Creates an OTLP gRPC LogRecordExporter with the specified configuration.
     *
     * @param headers Additional headers to be included in the gRPC requests.
     * @param cert    Trusted certificates for the gRPC connection.
     * @return A configured LogRecordExporter instance.
     */
    public LogRecordExporter createOtlpGrpcLogRecordExporter(Map<String, String> headers, byte[] cert) {
        OtlpGrpcLogRecordExporterBuilder builder = OtlpGrpcLogRecordExporter.builder().setEndpoint(backendUrl).setTimeout(transportTimeout, TimeUnit.MILLISECONDS);

        HeadersSupplier supplier = HeadersSupplier.INSTANCE;
        builder.setHeaders(supplier::getHeaders);
        if (headers != null) {
            supplier.updateHeaders(headers);
        }
        if (cert != null) {
            builder.setTrustedCertificates(cert);
        }

        return builder.build();
    }

    /**
     * Creates an OTLP HTTP metric exporter with the specified configuration.
     *
     * @param headers Additional headers to include
     * @param cert    Trusted certificates
     * @return OTLP HTTP metric exporter
     */
    public MetricExporter createOtlpHttpMetricExporter(Map<String, String> headers, byte[] cert) {
        OtlpHttpMetricExporterBuilder builder = OtlpHttpMetricExporter.builder().setEndpoint(backendUrl + METRICS_SUFFIX).setTimeout(transportTimeout, TimeUnit.MILLISECONDS);

        HeadersSupplier supplier = HeadersSupplier.INSTANCE;
        builder.setHeaders(supplier::getHeaders);
        if (headers != null) {
            supplier.updateHeaders(headers);
        }
        if (cert != null) {
            builder.setTrustedCertificates(cert);
        }
        return builder.build();
    }

    /**
     * Creates an OTLP HTTP Span Exporter with the specified backend URL, timeout, headers, and certificate.
     *
     * @param headers A map of additional headers to be included in the HTTP requests.
     * @param cert    The trusted certificates for the HTTPS connection.
     * @return An instance of SpanExporter configured with the provided parameters.
     */
    public SpanExporter createOtlpHttpTraceExporter(Map<String, String> headers, byte[] cert) {
        OtlpHttpSpanExporterBuilder builder = OtlpHttpSpanExporter.builder().setEndpoint(backendUrl + TRACES_SUFFIX).setTimeout(transportTimeout, TimeUnit.MILLISECONDS);

        HeadersSupplier supplier = HeadersSupplier.INSTANCE;
        builder.setHeaders(supplier::getHeaders);
        if (headers != null) {
            supplier.updateHeaders(headers);
        }
        if (cert != null) {
            builder.setTrustedCertificates(cert);
        }

        return builder.build();
    }

    /**
     * Creates an OTLP HTTP Log Record Exporter with the specified backend URL, timeout, headers, and certificate.
     *
     * @param headers A map of additional headers to be included in the HTTP requests.
     * @param cert    The trusted certificates for the HTTPS connection.
     * @return An instance of LogRecordExporter configured with the provided parameters.
     */
    public LogRecordExporter createOtlpHttpLogRecordExporter(Map<String, String> headers, byte[] cert) {
        OtlpHttpLogRecordExporterBuilder builder = OtlpHttpLogRecordExporter.builder().setEndpoint(backendUrl + LOGS_SUFFIX).setTimeout(transportTimeout, TimeUnit.MILLISECONDS);

        HeadersSupplier supplier = HeadersSupplier.INSTANCE;
        builder.setHeaders(supplier::getHeaders);
        if (headers != null) {
            supplier.updateHeaders(headers);
        }
        if (cert != null) {
            builder.setTrustedCertificates(cert);
        }

        return builder.build();
    }

    //private static MetricReader prometheusMetricReader = null;

    private static final Predicate<String> dftResAttrsFilterForPrometheus = (String key) -> DcUtil.OJR_PLUGIN.equals(key) || HostIncubatingAttributes.HOST_NAME.getKey().equals(key);

    /**
     * Returns the filter for resource attributes used when exporting metrics to Prometheus.
     *
     * @param resAttrsFilter The default filter for resource attributes used when exporting metrics to Prometheus.
     * @return The predicate used to filter resource attributes.
     */
    @Override
    public Predicate<String> updateResAttrsFilterForPrometheus(Predicate<String> resAttrsFilter) {
        return resAttrsFilter;
    }

    /*
    private synchronized MetricReader createPrometheusMetricReader0() {
        if (prometheusMetricReader != null) {
            return prometheusMetricReader;
        }
        PrometheusHttpServerBuilder builder = PrometheusHttpServer.builder().setPort(prometheusPort).setOtelScopeEnabled(false);
        if (prometheusHost != null && !prometheusHost.isEmpty()) {
            builder.setHost(prometheusHost);
        }

        builder.setAllowedResourceAttributesFilter(updateResAttrsFilterForPrometheus(dftResAttrsFilterForPrometheus));

        return prometheusMetricReader = builder.build();
    }
    */

    private static OjrPrometheusHttpServer prometheusHttpServer = null;


    public synchronized OjrPrometheusHttpServer createPrometheusHttpServerIfNotExist() {
        if (prometheusHttpServer != null) {
            return prometheusHttpServer; // Return early if the server is already created
        }
        prometheusHttpServer = new OjrPrometheusHttpServer(prometheusHost, prometheusPort, null, MemoryMode.REUSABLE_DATA, prometheusMetricRestrictions);
        return prometheusHttpServer;
    }

    /**
     * Creates and configures a Prometheus metric reader.
     * <p>
     * This method sets up a Prometheus HTTP server with a specified port.
     * Optionally, if a host is provided and not empty, it sets the host as well.
     *
     * @return A configured PrometheusMetricReader instance for Prometheus metrics.
     */
    public PrometheusMetricReader createPrometheusMetricReader() {
        return new PrometheusMetricReader(false, updateResAttrsFilterForPrometheus(dftResAttrsFilterForPrometheus));
    }


    /**
     * Initializes Prometheus for the OpenTelemetry Meter Provider.
     *
     * @param builder The SdkMeterProviderBuilder instance to configure Prometheus.
     * @return The updated SdkMeterProviderBuilder instance.
     */
    public SdkMeterProviderBuilder initPrometheus(SdkMeterProviderBuilder builder) {
        PrometheusMetricReader reader = createPrometheusMetricReader();
        builder.registerMetricReader(reader);
        createPrometheusHttpServerIfNotExist().registerReader(reader).start();
        return builder;
    }

    /**
     * Creates a default SDK meter provider with the specified configuration.
     *
     * @param resource Resource information
     * @return SDK meter provider
     */
    @Override
    public SdkMeterProvider getDefaultSdkMeterProvider(Resource resource) {
        Map<String, String> headers = DcUtil.getHeadersFromEnv();
        byte[] cert = getCert();

        if (transport.contains(DcUtil.GRPC)) {
            SdkMeterProviderBuilder builder = SdkMeterProvider.builder().setResource(resource).registerMetricReader(PeriodicMetricReader.builder(createOtlpGrpcMetricExporter(headers, cert)).setInterval(Duration.ofSeconds(callbackInterval)).build());
            if (transport.contains(DcUtil.PROMETHEUS)) {
                initPrometheus(builder);
            }
            return builder.build();
        } else if (transport.contains(DcUtil.HTTP)) {
            SdkMeterProviderBuilder builder = SdkMeterProvider.builder().setResource(resource).registerMetricReader(PeriodicMetricReader.builder(createOtlpHttpMetricExporter(headers, cert)).setInterval(Duration.ofSeconds(callbackInterval)).build());
            if (transport.contains(DcUtil.PROMETHEUS)) {
                initPrometheus(builder);
            }
            return builder.build();
        } else if (transport.contains(DcUtil.PROMETHEUS)) {
            return initPrometheus(SdkMeterProvider.builder().setResource(resource)).build();
        } else {
            return SdkMeterProvider.builder().build();
        }
    }

    /**
     * Returns the default SDK tracer provider configured with the given parameters.
     *
     * @param resource The resource to associate with the tracer provider.
     * @return The default SDK tracer provider.
     */
    @Override
    public SdkTracerProvider getDefaultSdkTraceProvider(Resource resource) {
        Map<String, String> headers = DcUtil.getHeadersFromEnv();
        byte[] cert = getCert();

        if (transport.contains(DcUtil.GRPC)) {
            return SdkTracerProvider.builder().addSpanProcessor(BatchSpanProcessor.builder(createOtlpGrpcTraceExporter(headers, cert)).setScheduleDelay(transportDelay, TimeUnit.MILLISECONDS).build()).setResource(resource).build();
        } else if (transport.contains(DcUtil.HTTP)) {
            return SdkTracerProvider.builder().addSpanProcessor(BatchSpanProcessor.builder(createOtlpHttpTraceExporter(headers, cert)).setScheduleDelay(transportDelay, TimeUnit.MILLISECONDS).build()).setResource(resource).build();
        } else {
            return SdkTracerProvider.builder().build();
        }
    }

    /**
     * Returns the default SDK logger provider configured with the given parameters.
     *
     * @param resource The resource to associate with the logger provider.
     * @return The default SDK logger provider.
     */
    @Override
    public SdkLoggerProvider getDefaultSdkLogProvider(Resource resource) {
        Map<String, String> headers = DcUtil.getHeadersFromEnv();
        byte[] cert = getCert();

        if (transport.contains(DcUtil.GRPC)) {
            return SdkLoggerProvider.builder().addLogRecordProcessor(BatchLogRecordProcessor.builder(createOtlpGrpcLogRecordExporter(headers, cert)).setScheduleDelay(transportDelay, TimeUnit.MILLISECONDS).build()).setResource(resource).build();
        } else if (transport.contains(DcUtil.HTTP)) {
            return SdkLoggerProvider.builder().addLogRecordProcessor(BatchLogRecordProcessor.builder(createOtlpHttpLogRecordExporter(headers, cert)).setScheduleDelay(transportDelay, TimeUnit.MILLISECONDS).build()).setResource(resource).build();
        } else {
            return SdkLoggerProvider.builder().build();
        }

    }

    /**
     * Reads and sets the built-in configuration parameters from a provided properties map.
     * The method uses default values if certain properties are not present. The default order of execution is: readBuiltinParameters,
     * readExtraParameters, initOnce, processParameters.
     *
     * @param properties A map containing configuration properties.
     * @param config     An instance of the Cfg class (not used in this method).
     */
    @Override
    public void readBuiltinParameters(Map<String, Object> properties, Cfg config) {
        setPollInterval((Integer) properties.getOrDefault(DcUtil.OTEL_POLLING_INTERVAL, DcUtil.DEFAULT_OTEL_POLL_INTERVAL));
        setCallbackInterval((Integer) properties.getOrDefault(DcUtil.OTEL_CALLBACK_INTERVAL, DcUtil.DEFAULT_OTEL_CALLBACK_INTERVAL));

        setBackendUrl((String) properties.getOrDefault(DcUtil.OTEL_BACKEND_URL, DcUtil.DEFAULT_OTEL_BACKEND_URL));
        setTransport((String) properties.getOrDefault(DcUtil.OTEL_TRANSPORT, DcUtil.DEFAULT_OTEL_TRANSPORT));

        setMetricRestrictions((String) properties.get(DcUtil.OTEL_RESTRICTED_METRICS));

        setPrometheusPort((Integer) properties.getOrDefault(DcUtil.PROMETHEUS_PORT, DcUtil.DEFAULT_PROMETHEUS_PORT));
        setPrometheusHost((String) properties.get(DcUtil.PROMETHEUS_HOST));
        setPrometheusMetricRestrictions((String) properties.get(DcUtil.PROMETHEUS_RESTRICTED_METRICS));

        setServiceName((String) properties.getOrDefault(DcUtil.OTEL_SERVICE_NAME, DcUtil.DEFAULT_OTEL_SERVICE_NAME));
        setServiceInstanceId((String) properties.get(DcUtil.OTEL_SERVICE_INSTANCE_ID));

        setTransportTimeout((Long) properties.getOrDefault(DcUtil.OTEL_TRANSPORT_TIMEOUT, DcUtil.DEFAULT_OTEL_TRANSPORT_TIMEOUT));
        setTransportDelay((Long) properties.getOrDefault(DcUtil.OTEL_TRANSPORT_DELAY, DcUtil.DEFAULT_OTEL_TRANSPORT_DELAY));
    }

    /**
     * Run this initialization just one time for this Data Collector of whatever any number of instances. The default order
     * of execution is: readBuiltinParameters, readExtraParameters, initOnce, processParameters.
     */
    @Override
    public void initOnce() throws Exception {
    }

    private final AtomicBoolean once = new AtomicBoolean(false);

    /**
     * Initializes the data collector.
     *
     * @throws Exception if an error occurs during initialization.
     */
    @Override
    public void initEnv(Map<String, Object> properties, Cfg config) throws Exception {
        getRawMetricsMap().putAll(provideInitRawMetricsMap());
        readBuiltinParameters(properties, config);
        readExtraParameters(properties, config);
        synchronized (once) {
            if (!once.get()) {
                once.set(true);
                initOnce();
            }
        }
        processParameters(properties, config);
    }

    /**
     * Retrieves the resource attributes for the data collection.
     *
     * @return a Resource object containing the attributes.
     */
    public Resource retrieveResourceAttributes() {
        Resource resource = Resource.create(Attributes.of(ServiceAttributes.SERVICE_NAME, serviceName, TelemetryAttributes.TELEMETRY_SDK_NAME, "ojr", TelemetryAttributes.TELEMETRY_SDK_LANGUAGE, "java", TelemetryAttributes.TELEMETRY_SDK_VERSION, DcUtil.OCR_VERSION));

        ResourceEnricher enricher = new ResourceEnricher(resource);
        enricher.enrich(ServiceIncubatingAttributes.SERVICE_INSTANCE_ID, serviceInstanceId);
        enrichResourceAttributes(enricher);
        return enricher.getResource();
    }

    /**
     * Enriches the resource with the host name attribute if the host name is valid.
     *
     * @param enricher The ResourceEnricher instance used to add attributes to the resource.
     */
    public void enrichHostNameInResource(ResourceEnricher enricher) {
        String hostname = getHostname();
        if (hostname != null && !hostname.equals(DcUtil.N_A))
            enricher.enrich(HostIncubatingAttributes.HOST_NAME, getHostname());
    }

    /**
     * Enriches the resource with the process ID attribute if the process ID is valid.
     *
     * @param enricher The ResourceEnricher instance used to add attributes to the resource.
     */
    public void enrichProcessIdInResource(ResourceEnricher enricher) {
        long pid = getPid();
        if (pid >= 0) {
            enricher.enrich(ProcessIncubatingAttributes.PROCESS_PID, pid);
        }
    }

    /**
     * Enriches the resource with the container ID attribute if the container ID is valid.
     *
     * @param enricher The ResourceEnricher instance used to add attributes to the resource.
     */
    public void enrichContainerIdInResource(ResourceEnricher enricher) {
        String containerId = getContainerId();
        if (containerId != null && !containerId.equals(DcUtil.N_A))
            enricher.enrich(ContainerIncubatingAttributes.CONTAINER_ID, containerId);
    }

    /**
     * Initializes meters using the provided OpenTelemetry instance.
     *
     * @param openTelemetry OpenTelemetry instance
     */
    @Override
    public void initMeters(OpenTelemetry openTelemetry) {
        // Create a default meter and add it to the meters map
        Meter defaultMeter = openTelemetry.meterBuilder("ojr.sdk").setInstrumentationVersion("1.0.0").build();
        meters.put(DcUtil.DEFAULT, defaultMeter);
    }

    /**
     * Initializes the OpenTelemetry engine with specified features.
     *
     * @param useMetrics a flag indicating whether to enable metrics collection
     * @param useTraces  a flag indicating whether to enable trace collection
     * @param useLogs    a flag indicating whether to enable log collection
     */
    @Override
    public void initOTelEngine(boolean useMetrics, boolean useTraces, boolean useLogs) {
        Resource resource = retrieveResourceAttributes();
        OpenTelemetrySdkBuilder builder = OpenTelemetrySdk.builder();

        if (useMetrics) builder.setMeterProvider(getDefaultSdkMeterProvider(resource));

        if (useTraces) builder.setTracerProvider(getDefaultSdkTraceProvider(resource));

        if (useLogs) builder.setLoggerProvider(getDefaultSdkLogProvider(resource));

        OpenTelemetry openTelemetry = builder.build();
        initMeters(openTelemetry);

        if (useMetrics) {
            registerMetrics();
        }
    }

    private ScheduledExecutorService scheduler;

    /**
     * Returns the scheduled executor service used for data collection process.
     *
     * @return a ScheduledExecutorService instance
     */
    public ScheduledExecutorService getScheduledExecutorService() {
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        return scheduler;
    }

    /**
     * Starts the data collection process.
     */
    @Override
    public void start() {
        getScheduledExecutorService().scheduleWithFixedDelay(this::collectData, 1, pollInterval, TimeUnit.SECONDS);
    }
}