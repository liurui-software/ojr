package com.ojr.vault.services.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.ojr.vault.VaultService;
import com.ojr.vault.VaultServiceConfig;
import com.ojr.vault.services.vault.exception.VaultSecretException;
import com.ojr.vault.services.vault.util.Constant;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Base64;

public class VaultConfig {

    private static final Logger LOGGER = Logger.getLogger(VaultConfig.class.getName());

    private static Vault vaultClient;

    public static <T extends VaultService> T executeStep(T config) throws VaultException {
        setVaultClient(config.getVaultServiceConfig());
        return updateVaultSecretConfig(config);
    }

    private static void setVaultClient(VaultServiceConfig config) {
        vaultClient = VaultClient.createVaultClient(config);
    }

    private static String fetchSecret(Map<String, Object> vaultSecrets, VaultServiceConfig config) throws VaultException {
        String key = (String) vaultSecrets.get(Constant.VAULT_SECRET_KEY);
        String path = (String) vaultSecrets.get(Constant.VAULT_SECRET_PATH);
        String secret = read(path, key);
        if (null == secret || secret.isEmpty()) {
            throw new VaultSecretException("Retrieved secret was empty");
        }
        LOGGER.log(Level.INFO, "Fetched secret from vault successfully path: " + path);
        return Base64.getEncoder().encodeToString(secret.getBytes());
    }

    @SuppressWarnings("unchecked")
    private static <T extends VaultService> T updateVaultSecretConfig(T config) throws VaultException {
        for (Map<String, Object> instance : config.getInstances()) {
            for (Map.Entry<String, Object> prop : instance.entrySet()) {
                if (prop.getValue() instanceof Map) {
                    Map<String, Object> vaultSecrets = (Map<String, Object>) prop.getValue();
                    for (Map.Entry<String, Object> secret : vaultSecrets.entrySet()) {
                        if (secret.getKey().contains(Constant.VAULT_SECRET)) {
                            LOGGER.log(Level.INFO, "Updating the secret for instance with address: " + instance.get("db.address"));
                            instance.put(prop.getKey(), fetchSecret(vaultSecrets, config.getVaultServiceConfig()));
                            break;
                        }
                    }
                }
            }
        }
        return config;
    }

    public static String read(final String path, final String keyName) throws VaultException {
        try {
            return vaultClient.logical().read(path).getData().get(keyName);
        } catch (VaultException e) {
            LOGGER.info("Error occurred during the Vault secret retrieval path:" + path + " key: " + keyName);
            LOGGER.log(Level.SEVERE, "Exception: ", e.getMessage());
        }
        return null;
    }

}
