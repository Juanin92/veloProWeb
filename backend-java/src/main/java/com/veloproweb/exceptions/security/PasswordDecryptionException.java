package com.veloproweb.exceptions.security;

public class PasswordDecryptionException extends RuntimeException {
    public PasswordDecryptionException(String message) {
        super(message);
    }
}
