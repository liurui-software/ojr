package com.ojr.vault.services.vault;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.ojr.vault.VaultServiceConfig;
import com.ojr.vault.services.vault.auth.AuthenticationFactory;
import com.ojr.vault.services.vault.auth.strategy.VaultAuthenticationStrategy;
import com.ojr.vault.services.vault.util.Constant;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to create the Vault client
 */
public class VaultClient {

    private VaultClient() {
        //Private Constructor
    }

    private static final Logger LOGGER = Logger.getLogger(VaultClient.class.getName());

    public static Vault createVaultClient(VaultServiceConfig vaultServiceConfig) {
        try {
            final VaultConfig vaultConfig = new VaultConfig().address(vaultServiceConfig.getConnectionURL());

            if (vaultServiceConfig.getConnectionURL().startsWith(Constant.HTTPS) && vaultServiceConfig.isPathToPEMFilePresent()) {
                SslConfig sslConfig = new SslConfig().pemFile(new File(vaultServiceConfig.getPathToPEMFile().get())).build();
                vaultConfig.sslConfig(sslConfig);
                LOGGER.info("SSL for Vault is set");
            }

            VaultAuthenticationStrategy vaultAuthenticationStrategy
                    = AuthenticationFactory.getVaultAuthStrategyFromConfig(vaultServiceConfig);

            if (vaultAuthenticationStrategy.token().isPresent()) {
                vaultConfig.token(vaultAuthenticationStrategy.token().get());
                return new Vault(vaultConfig.build(), vaultServiceConfig.getKvVersion());
            }
        } catch (VaultException e) {
            LOGGER.log(Level.SEVERE, "An exception occurred while creating vault client: {}", e.getMessage());
        }
        return null;
    }
}
