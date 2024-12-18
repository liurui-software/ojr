package com.ojr.informix.metrics.strategy;

import com.ojr.informix.OnstatCommandExecutor;
import com.ojr.informix.metrics.MetricDataConfig;

import java.util.Optional;

public class CommandExecutorStrategy extends MetricsExecutionStrategy {
    private final OnstatCommandExecutor onstatCommandExecutor;

    protected CommandExecutorStrategy(OnstatCommandExecutor onstatCommandExecutor) {
        this.onstatCommandExecutor = onstatCommandExecutor;
    }

    @SuppressWarnings("unchecked")
    protected <T> T collectMetrics(MetricDataConfig metricDataConfig) {
        return (T) collectMetricsUsingCMD(metricDataConfig, onstatCommandExecutor);
    }

    private Number collectMetricsUsingCMD(MetricDataConfig metricDataConfig, OnstatCommandExecutor onstatCommandExecutor) {
        Optional<String[]> result = onstatCommandExecutor.executeCommand(metricDataConfig.getScriptName());
        if (result.isPresent()) {
            if(TypeChecker.isDouble(metricDataConfig.getReturnType())){
                return Double.parseDouble(result.get()[0]);
            }
            else if (TypeChecker.isNumber(metricDataConfig.getReturnType())) {
                return Integer.parseInt(result.get()[0]);
            }
        }
        return null;
    }
}
