package com.ojr.host;

import com.ojr.core.AbstractDc;
import com.ojr.core.ResourceEnricher;
import com.ojr.core.metric.RawMetric;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;

import java.util.Map;


public abstract class AbstractHostDc<Cfg extends HostDcConfig> extends AbstractDc<Cfg> {
    public final static String INSTRUMENTATION_SCOPE_PREFIX = "otelcol/hostmetricsreceiver/";


    @Override
    public Map<String, RawMetric> provideInitRawMetricsMap() {
        return new HostRawMetricRegistry().getMap();
    }

    @Override
    public void readExtraParameters(Map<String, Object> properties, Cfg dcConfig) {
    }

    @Override
    public void enrichResourceAttributes(ResourceEnricher enricher) {
    }

    /* The purpose to overwrite this method is to comply with the "hostmetrics" receiver of
     * OpenTelemetry Contrib Collector.
     **/
    @Override
    public void initMeters(OpenTelemetry openTelemetry) {
        super.initMeters(openTelemetry);
        initMeter(openTelemetry, HostDcUtil.MeterName.CPU);
        initMeter(openTelemetry, HostDcUtil.MeterName.MEMORY);
        initMeter(openTelemetry, HostDcUtil.MeterName.NETWORK);
        initMeter(openTelemetry, HostDcUtil.MeterName.LOAD);
        initMeter(openTelemetry, HostDcUtil.MeterName.DISK);
        initMeter(openTelemetry, HostDcUtil.MeterName.FILESYSTEM);
        initMeter(openTelemetry, HostDcUtil.MeterName.PROCESSES);
        initMeter(openTelemetry, HostDcUtil.MeterName.PAGING);
    }

    protected void initMeter(OpenTelemetry openTelemetry, String name) {
        Meter meter1 = openTelemetry.meterBuilder(INSTRUMENTATION_SCOPE_PREFIX + name).setInstrumentationVersion("1.0.0").build();
        getMeters().put(name, meter1);
    }

}
