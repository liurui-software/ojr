package com.ojr.vault.services.vault.auth.strategy;

import com.bettercloud.vault.Vault;
import java.util.Optional;

/**
 * Base Interface of Different Authentication Strategy
 */
public interface VaultAuthenticationStrategy {

    default Optional<String> authenticate(Vault vault) {
        return Optional.empty();
    }

    default Optional<String> token() {
        return Optional.empty();
    }
}
