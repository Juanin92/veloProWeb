package com.veloProWeb.exceptions.communication;

public class MessageAlreadyReadException extends RuntimeException {
    public MessageAlreadyReadException(String message) {
        super(message);
    }
}
