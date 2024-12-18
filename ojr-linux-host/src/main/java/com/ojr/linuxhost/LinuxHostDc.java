package com.ojr.linuxhost;

import com.ojr.core.DcUtil;
import com.ojr.core.ResourceEnricher;
import com.ojr.host.AbstractHostDc;
import com.ojr.host.HostDcConfig;
import com.ojr.host.HostDcUtil;
import io.opentelemetry.semconv.incubating.HostIncubatingAttributes;
import io.opentelemetry.semconv.incubating.OsIncubatingAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ojr.host.HostDcUtil.*;

/**
 * Represents a data collector for Dameng Database, extending the functionality of AbstractDbDc.
 * This class is responsible for processing database parameters, registering metrics, and collecting data from the Dameng database.
 */
public class LinuxHostDc extends AbstractHostDc<HostDcConfig> {
    private static final Logger logger = Logger.getLogger(LinuxHostDc.class.getName());

    @Override
    public void processParameters(Map<String, Object> map, HostDcConfig hostDcConfig) throws Exception {
    }

    @Override
    public void enrichResourceAttributes(ResourceEnricher enricher) {
        super.enrichResourceAttributes(enricher);

        enrichHostNameInResource(enricher);

        enricher.enrich(DcUtil.OJR_PLUGIN, "linux-host");
        enricher.enrich(OsIncubatingAttributes.OS_TYPE, "linux");
        enricher.enrich(HostIncubatingAttributes.HOST_ID, getHostId());
    }

    // Collect data from the database and update metrics
    @Override
    public void collectData() {
        logger.info("Start to collect metrics");
        getRawMetric(SYSTEM_CPU_TIME_NAME).setValue(LinuxHostUtil.getCpuTimeResults());
        getRawMetric(SYSTEM_MEMORY_USAGE_NAME).setValue(LinuxHostUtil.getMemUsageResults());

        List<Double> loads;
        try {
            loads = LinuxHostUtil.getLoadAvgInfo();
            getRawMetric(SYSTEM_CPU_LOAD1_NAME).setValue(loads.get(0));
            getRawMetric(SYSTEM_CPU_LOAD5_NAME).setValue(loads.get(1));
            getRawMetric(SYSTEM_CPU_LOAD15_NAME).setValue(loads.get(2));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Cannot record loads", e);
        }
    }

    public String getHostId() {
        try {
            return HostDcUtil.readFileText("/etc/machine-id");
        } catch (IOException e) {
            return DcUtil.N_A;
        }
    }

}
