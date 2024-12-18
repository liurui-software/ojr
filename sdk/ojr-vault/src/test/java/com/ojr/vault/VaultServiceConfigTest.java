package com.ojr.vault;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class VaultServiceConfigTest {

    private VaultServiceConfig config;

    @BeforeEach
    public void setUp() throws Exception {
        config = new VaultServiceConfig();
        File file = new File(getClass().getClassLoader().getResource("vault_service_config.json").toURI());
        String json = new String(java.nio.file.Files.readAllBytes(file.toPath()));
        ObjectMapper mapper = new ObjectMapper();
        config = mapper.readValue(json, VaultServiceConfig.class);
    }

    @Test
    public void testGetConnectionURL() {
        assertEquals("http://localhost:8200", config.getConnectionURL());
    }

    @Test
    public void testGetAuthConfig() {
        Map<String, Object> expectedAuthConfig = new HashMap<>();
        expectedAuthConfig.put("token", "s.1234567890");

        assertEquals(expectedAuthConfig, config.getAuthConfig());
    }

    @Test
    public void testGetSecretRefreshRate() {
        assertEquals(2, config.getSecretRefreshRate());
    }

    @Test
    public void testGetKvVersion() {
        assertEquals(2, config.getKvVersion());
    }

    @Test
    public void testGetPathToPEMFile() {
        assertTrue(config.getPathToPEMFile().isPresent());
        assertEquals("/path/to/pemfile.pem", config.getPathToPEMFile().get());
    }

    @Test
    public void testIsPathToPEMFilePresent() {
        assertTrue(config.isPathToPEMFilePresent());
    }
}
