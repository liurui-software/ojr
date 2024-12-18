package com.ojr.vault.services.vault.exception;

public class VaultSecretException extends RuntimeException {

    public VaultSecretException(String message) {
        super(message);
    }
}
