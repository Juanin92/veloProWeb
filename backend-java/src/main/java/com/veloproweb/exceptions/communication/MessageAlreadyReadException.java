package com.veloproweb.exceptions.communication;

public class MessageAlreadyReadException extends RuntimeException {
    public MessageAlreadyReadException(String message) {
        super(message);
    }
}
