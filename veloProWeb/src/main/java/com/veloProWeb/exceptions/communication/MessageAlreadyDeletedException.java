package com.veloProWeb.exceptions.communication;

public class MessageAlreadyDeletedException extends RuntimeException {
    public MessageAlreadyDeletedException(String message) {
        super(message);
    }
}
