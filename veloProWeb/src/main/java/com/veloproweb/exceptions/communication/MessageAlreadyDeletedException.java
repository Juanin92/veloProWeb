package com.veloproweb.exceptions.communication;

public class MessageAlreadyDeletedException extends RuntimeException {
    public MessageAlreadyDeletedException(String message) {
        super(message);
    }
}
