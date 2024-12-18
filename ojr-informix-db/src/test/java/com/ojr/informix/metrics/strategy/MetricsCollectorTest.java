package com.ojr.informix.metrics.strategy;

import com.ojr.informix.OnstatCommandExecutor;
import com.ojr.informix.metrics.MetricCollectionMode;
import com.ojr.informix.metrics.MetricDataConfig;
import com.ojr.informix.metrics.MetricsDataConfigRegister;
import com.ojr.rdb.DbDcUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

public class MetricsCollectorTest {

    private static BasicDataSource dataSource;
    private static OnstatCommandExecutor onstatCommandExecutor;
    private static MetricsCollector metricsCollector;

    @BeforeAll
    public static void init() {
        dataSource = mock(BasicDataSource.class);
        onstatCommandExecutor = mock(OnstatCommandExecutor.class);
        metricsCollector = new MetricsCollector(dataSource, onstatCommandExecutor);
    }

    @Test
    public void shouldCollectMetricsWithCMD() {
        MetricsDataConfigRegister.subscribeMetricDataConfig("metric",
                new MetricDataConfig("query", MetricCollectionMode.CMD, Number.class));
        given(onstatCommandExecutor.executeCommand(any())).willReturn(Optional.of(new String[]{"1"}));
        assertEquals((Number) 1, metricsCollector.collectMetrics("metric"));
    }
}
