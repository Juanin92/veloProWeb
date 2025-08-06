package com.veloproweb.exceptions.handlers;

import com.veloproweb.exceptions.BaseExceptionHandler;
import com.veloproweb.exceptions.security.PasswordDecryptionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

public class SecurityExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(PasswordDecryptionException.class)
    public ResponseEntity<Map<String, String>> handlePasswordDecryptionException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }
}
