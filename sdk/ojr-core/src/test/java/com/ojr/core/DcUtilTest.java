package com.ojr.core;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.ObservableDoubleMeasurement;
import io.opentelemetry.api.metrics.ObservableLongMeasurement;
import io.opentelemetry.sdk.resources.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DcUtilTest {

    @Mock
    private Meter meter;

    @Mock
    private ObservableLongMeasurement observableLongMeasurement;

    @Mock
    private ObservableDoubleMeasurement observableDoubleMeasurement;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMergeResourceAttributesFromEnv() {
        System.setProperty("OTEL_RESOURCE_ATTRIBUTES", "key1=value1,key2=value2");
        Resource resource = Resource.getDefault();
        Resource mergedResource = DcUtil.mergeResourceAttributesFromEnv(resource);

        assertEquals("value1", mergedResource.getAttribute(AttributeKey.stringKey("key1")));
        assertEquals("value2", mergedResource.getAttribute(AttributeKey.stringKey("key2")));
    }

    @Test
    public void testGetHeadersFromEnv() {
        System.setProperty("OTEL_EXPORTER_OTLP_HEADERS", "header1=value1,header2=value2");
        Map<String, String> headers = DcUtil.getHeadersFromEnv();

        assertNotNull(headers);
        assertEquals("value1", headers.get("header1"));
        assertEquals("value2", headers.get("header2"));
    }

    @Test
    public void testGetCert() throws IOException {
        String tempFilePath = createTempCertFile();
        System.setProperty("OTEL_EXPORTER_OTLP_CERTIFICATE", tempFilePath);
        byte[] cert = DcUtil.getCert();

        assertNotNull(cert);
        assertTrue(cert.length > 0);
    }

    private String createTempCertFile() throws IOException {
        File tempFile = File.createTempFile("cert", ".pem");
        Files.write(tempFile.toPath(), "-----BEGIN CERTIFICATE-----\nMIIDXTCCAkWgAwIBAgIJALwX".getBytes());
        return tempFile.getAbsolutePath();
    }

    @Test
    public void testConvertMapToAttributes() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", 100L);
        map.put("key2", 100.0);
        map.put("key3", true);
        map.put("key4", "value");

        Attributes attributes = DcUtil.convertMapToAttributes(map);

        assertEquals(100L, attributes.get(AttributeKey.longKey("key1")));
        assertEquals(100.0, attributes.get(AttributeKey.doubleKey("key2")));
        assertEquals(true, attributes.get(AttributeKey.booleanKey("key3")));
        assertEquals("value", attributes.get(AttributeKey.stringKey("key4")));
    }

    @Test
    public void testGetPid() {
        long pid = DcUtil.getPid();
        assertTrue(pid >= 0);
    }

    @Test
    public void testBase64Decode() {
        String encodedStr = Base64.getEncoder().encodeToString("test".getBytes());
        String decodedStr = DcUtil.base64Decode(encodedStr);

        assertEquals("test", decodedStr);
    }
}