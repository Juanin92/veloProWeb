package com.veloproweb.exceptions.handlers;

import com.veloproweb.exceptions.BaseExceptionHandler;
import com.veloproweb.exceptions.data.LocalDataNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class LocalDataExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(LocalDataNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleLocalDataNotFound(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
