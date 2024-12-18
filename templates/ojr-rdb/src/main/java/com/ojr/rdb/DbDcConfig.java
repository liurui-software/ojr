package com.ojr.rdb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ojr.core.BasicDcConfig;
import com.ojr.vault.VaultService;
import com.ojr.vault.VaultServiceConfig;

import java.util.Optional;

public class DbDcConfig extends BasicDcConfig implements VaultService {
    /* DB related configuration:
     */

    @JsonProperty("db.system")
    private String dbSystem;

    @JsonProperty("db.driver")
    private String dbDriver;

    public String getDbSystem() {
        return dbSystem;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    /* Vault related configuration:
     */

    @JsonProperty(value = "vault", required = false)
    private VaultServiceConfig vaultConfig;

    @Override
    @JsonIgnore
    public boolean isVaultServiceConfigPresent() {
        return Optional.ofNullable(vaultConfig).isPresent();
    }

    @Override
    public VaultServiceConfig getVaultServiceConfig() {
        return vaultConfig;
    }
}
