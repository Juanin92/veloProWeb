package com.veloProWeb.exceptions.user;

public class EmailAlreadyRegisterException extends RuntimeException {
    public EmailAlreadyRegisterException(String message) {
        super(message);
    }
}
