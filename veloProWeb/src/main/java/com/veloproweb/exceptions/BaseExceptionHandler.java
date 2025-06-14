package com.veloproweb.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseExceptionHandler {

    /**
     * Construye una respuesta de error personalizada.
     * @param message - mensaje de error a incluir en la respuesta
     * @param status - código de estado HTTP a incluir en la respuesta
     * @return - ResponseEntity con el mensaje de error y el código de estado
     */
    protected ResponseEntity<Map<String, String>> buildResponse(String message, HttpStatus status) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        errorResponse.put("status", status.name());
        return new ResponseEntity<>(errorResponse, status);
    }
}
