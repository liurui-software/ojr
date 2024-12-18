package com.ojr.core;

import com.ojr.core.metric.MetricCalculationMode;
import com.ojr.core.metric.MetricInstrumentType;
import com.ojr.core.metric.MetricQueryResult;
import com.ojr.core.metric.RawMetric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RawMetricTest {

    private RawMetric rawMetric;

    @BeforeEach
    public void setUp() {
        rawMetric = new RawMetric(MetricInstrumentType.COUNTER, "testMetric", "This is a test metric", "unit", true, "testAttributeKey");
    }

    @Test
    public void testSetCalculationMode_ShouldSetAndGetCorrectCalculationMode() {
        rawMetric.setCalculationMode(MetricCalculationMode.RATE);
        assertEquals(MetricCalculationMode.RATE, rawMetric.getCalculationMode());
    }

    @Test
    public void testSetRateUnit_ShouldSetAndGetCorrectRateUnit() {
        rawMetric.setRateUnit(100);
        assertEquals(100, rawMetric.getRateUnit());
    }

    @Test
    public void testPurgeOutdatedDps_ShouldRemoveOutdatedDataPoints() {
        RawMetric metric = new RawMetric(MetricInstrumentType.COUNTER, "testMetric", "This is a test metric", "unit", true, "testAttributeKey");
        metric.getDataPoint("testKey1").setValue(100);
        metric.getDataPoint("testKey2").setValue(200);
        metric.setOutdatedTime(50);
        metric.purgeOutdatedDps();
        Map<String, RawMetric.DataPoint> dataPoints = metric.getDataPoints();
        assertTrue(dataPoints.containsKey("testKey1"));
        assertTrue(dataPoints.containsKey("testKey2"));
    }

    @Test
    public void testSetOutdatedTime_ShouldSetAndGetCorrectOutdatedTime() {
        rawMetric.setOutdatedTime(1000);
        assertEquals(1000, rawMetric.getOutdatedTime());
    }

    @Test
    public void testSetValueWithNullValue_ShouldNotSetValues() {
        rawMetric.setValue((Long)null);
        assertNull(rawMetric.getDataPoint(null).getValue());
    }

    @Test
    public void testSetValueWithNonNullValue_ShouldSetValues() {
        rawMetric.setValue(50);
        assertNotNull(rawMetric.getDataPoint(null).getValue());
        assertEquals(50, rawMetric.getDataPoint(null).getValue());
    }

    @Test
    public void testSetValueWithAttributes_ShouldSetValuesAndAttributes() {
        MetricQueryResult result = new MetricQueryResult(50);
        result.setAttribute("testKey", "testValue");
        rawMetric.setValue(result);
        assertNotNull(rawMetric.getDataPoint(null).getValue());
        assertEquals(50, rawMetric.getDataPoint(null).getValue());
        assertEquals("testValue", rawMetric.getDataPoint(null).getAttributes().get("testKey"));
    }

    @Test
    public void testSetClearDps_ShouldSetAndGetCorrectClearDps() {
        rawMetric.setClearDps(true);
        assertTrue(rawMetric.isClearDps());
    }

    @Test
    public void testGetDataPoint_WithNullKey_ShouldReturnDefaultDataPoint() {
        assertNotNull(rawMetric.getDataPoint(null));
    }

    @Test
    public void testGetDataPoint_WithNonNullKey_ShouldReturnCorrectDataPoint() {
        String key = "testKey";
        assertNotNull(rawMetric.getDataPoint(key));
        assertEquals(key, rawMetric.getDataPoint(key).getKey());
    }
}