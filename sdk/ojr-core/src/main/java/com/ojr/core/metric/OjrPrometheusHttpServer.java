package com.ojr.core.metric;

import io.opentelemetry.exporter.prometheus.PrometheusMetricReader;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.common.export.MemoryMode;
import io.opentelemetry.sdk.internal.DaemonThreadFactory;
import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.model.registry.PrometheusRegistry;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Prometheus HTTP server implementation.
 * This class manages the Prometheus HTTP server, including starting, stopping, and shutting down.
 */
public class OjrPrometheusHttpServer {
    // HTTP server instance to handle incoming requests
    private HTTPServer httpServer;

    private final PrometheusMergedReader mReader;

    // Prometheus registry to manage and expose metrics
    private final PrometheusRegistry prometheusRegistry = new PrometheusRegistry();

    // Host address where the HTTP server will listen
    private final String host;

    // Port number where the HTTP server will listen
    private final int port;

    // Memory mode configuration (e.g., REUSABLE_DATA)
    private final MemoryMode memoryMode;

    // Executor service for handling HTTP server tasks
    private final ExecutorService executor;


    /**
     * Constructor to initialize the Prometheus HTTP server.
     *
     * @param host       Host address for the HTTP server
     * @param port       Port number for the HTTP server
     * @param executor   Optional executor service for handling tasks
     * @param memoryMode Memory mode configuration
     * @param prometricsMetricRestrictions The restricted metrics for Prometheus
     */
    public OjrPrometheusHttpServer(
            String host,
            int port,
            @Nullable ExecutorService executor,
            MemoryMode memoryMode,
            String[] prometricsMetricRestrictions) {

        this.host = host;
        this.port = port;
        this.memoryMode = memoryMode;
        this.executor = executor;
        mReader = new PrometheusMergedReader(prometricsMetricRestrictions);
    }

    /**
     * Registers a PrometheusMetricReader
     *
     * @param reader PrometheusMetricReader instance to register
     * @return The current OjrPrometheusHttpServer instance
     */
    public OjrPrometheusHttpServer registerReader(PrometheusMetricReader reader) {
        mReader.registerReader(reader);
        return this;
    }

    private static boolean isStarted = false;

    /**
     * Starts the Prometheus HTTP server.
     */
    public synchronized void start() {
        if (isStarted)
            return;

        prometheusRegistry.register(mReader);

        ExecutorService executor1 = executor;

        // If memory mode is REUSABLE_DATA, create a dedicated thread pool
        if (memoryMode == MemoryMode.REUSABLE_DATA) {
            executor1 =
                    new ThreadPoolExecutor(
                            1,
                            1,
                            0L,
                            TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<>(),
                            new DaemonThreadFactory("prometheus-http-server"));
        }
        try {
            // Build and start the HTTP server with the given configuration
            httpServer =
                    HTTPServer.builder()
                            .hostname(host)
                            .port(port)
                            .executorService(executor1)
                            .registry(prometheusRegistry)
                            .buildAndStart();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create Prometheus HTTP server", e);
        }

        isStarted = true;
    }

    public PrometheusMergedReader getmReader() {
        return mReader;
    }

    /**
     * Returns the current memory mode configuration.
     *
     * @return MemoryMode enum value
     */
    public MemoryMode getMemoryMode() {
        return memoryMode;
    }

    /**
     * Shuts down the Prometheus HTTP server gracefully.
     *
     * @return CompletableResultCode indicating the shutdown result
     */
    public CompletableResultCode shutdown() {
        CompletableResultCode rc = new CompletableResultCode();
        Runnable shutdownFunction =
                () -> {
                    try {
                        // Unregister each reader and stop the HTTP server
                        prometheusRegistry.unregister(mReader);
                        httpServer.stop();
                    } catch (Throwable t) {
                        rc.fail();
                    }
                };
        Thread shutdownThread = new Thread(shutdownFunction, "Shutdown-OjrPrometheusHttpServer");
        shutdownThread.setDaemon(true);
        shutdownThread.start();
        isStarted = false;
        return rc;
    }

    /**
     * Closes the Prometheus HTTP server by initiating a shutdown and waiting for completion.
     */
    public void close() {
        shutdown().join(10, TimeUnit.SECONDS);
    }

    /**
     * Returns a string representation of the server's address.
     *
     * @return String representation of the server's address
     */
    @Override
    public String toString() {
        return "OjrPrometheusHttpServer{address=" + getAddress() + "}";
    }

    /**
     * Gets the InetSocketAddress of the HTTP server.
     *
     * @return InetSocketAddress object
     */
    InetSocketAddress getAddress() {
        return new InetSocketAddress(host, httpServer.getPort());
    }
}
