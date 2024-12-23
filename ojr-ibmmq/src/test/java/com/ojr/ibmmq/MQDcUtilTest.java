package com.ojr.ibmmq;

import com.ojr.core.metric.MetricQueryResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MQDcUtilTest {

    @Test
    public void testGetQmgrMetadataResult() {
        // Arrange
        String version = "9.2.0.0";
        String platform = "Linux";
        MetricQueryResult expected = new MetricQueryResult(1);
        expected.setAttribute(MQDcUtil.VERSION, version);
        expected.setAttribute(MQDcUtil.PLATFORM, platform);

        // Act
        MetricQueryResult actual = MQDcUtil.getQmgrMetadataResult(version, platform);

        // Assert
        assertEquals(expected.getAttribute(MQDcUtil.VERSION), actual.getAttribute(MQDcUtil.VERSION));
        assertEquals(expected.getAttribute(MQDcUtil.PLATFORM), actual.getAttribute(MQDcUtil.PLATFORM));
    }

    @Test
    public void testGetQueueMetadataResult() {
        // Arrange
        String queue = "QUEUE1";
        String type = "LOCAL";
        String delivery = "PERSISTENT";
        String usage = "PRODUCTION";
        MetricQueryResult expected = new MetricQueryResult(1);
        expected.setAttribute(MQDcUtil.QUEUE, queue);
        expected.setAttribute(MQDcUtil.TYPE, type);
        expected.setAttribute(MQDcUtil.DELIVERY, delivery);
        expected.setAttribute(MQDcUtil.USAGE, usage);
        expected.setKey(queue);

        // Act
        MetricQueryResult actual = MQDcUtil.getQueueMetadataResult(queue, type, delivery, usage);

        // Assert
        assertEquals(expected.getAttribute(MQDcUtil.QUEUE), actual.getAttribute(MQDcUtil.QUEUE));
        assertEquals(expected.getAttribute(MQDcUtil.TYPE), actual.getAttribute(MQDcUtil.TYPE));
        assertEquals(expected.getAttribute(MQDcUtil.DELIVERY), actual.getAttribute(MQDcUtil.DELIVERY));
        assertEquals(expected.getAttribute(MQDcUtil.USAGE), actual.getAttribute(MQDcUtil.USAGE));
        assertEquals(expected.getKey(), actual.getKey());
    }

    @Test
    public void testGetSimpleQueueMetricResult() {
        // Arrange
        String queue = "QUEUE1";
        int depth = 10;
        MetricQueryResult expected = new MetricQueryResult(depth);
        expected.setAttribute(MQDcUtil.QUEUE, queue);
        expected.setKey(queue);

        // Act
        MetricQueryResult actual = MQDcUtil.getSimpleQueueMetricResult(queue, depth);

        // Assert
        assertEquals(expected.getAttribute(MQDcUtil.QUEUE), actual.getAttribute(MQDcUtil.QUEUE));
        assertEquals(expected.getKey(), actual.getKey());
    }
}
