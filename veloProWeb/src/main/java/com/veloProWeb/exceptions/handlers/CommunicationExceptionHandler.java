package com.veloProWeb.exceptions.handlers;

import com.veloProWeb.exceptions.BaseExceptionHandler;
import com.veloProWeb.exceptions.communication.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CommunicationExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(Exception e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            MessageAlreadyDeletedException.class,
            MessageAlreadyReadException.class,
            MessageReceiverUserException.class
    })
    public ResponseEntity<Map<String, String>> handleCommunicationExceptions(Exception e) {
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MessageAccessDeniedExcepcion.class)
    public ResponseEntity<Map<String, String>> handleCommunicationAccessDenied(Exception e) {
        return buildResponse(e.getMessage(), HttpStatus.FORBIDDEN);
    }
}
