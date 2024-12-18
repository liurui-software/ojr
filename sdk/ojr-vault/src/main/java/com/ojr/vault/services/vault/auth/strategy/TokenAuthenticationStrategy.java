package com.ojr.vault.services.vault.auth.strategy;

import java.util.Optional;

/**
 * This class will help to do the token based Authentication Strategy
 */
public class TokenAuthenticationStrategy implements VaultAuthenticationStrategy {
    private final String token;

    public TokenAuthenticationStrategy(final String token) {
        this.token = token;
    }

    @Override
    public Optional<String> token() {
        return Optional.ofNullable(token);
    }
}
