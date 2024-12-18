package com.ojr.vault;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base Interface which needs to be implemented by Different collector in order to integrate different vault strategy
 */
public interface VaultService {

    boolean isVaultServiceConfigPresent();

    VaultServiceConfig getVaultServiceConfig();

    List<ConcurrentHashMap<String, Object>> getInstances();
}
