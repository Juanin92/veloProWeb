package com.veloProWeb.exceptions.handlers;

import com.veloProWeb.exceptions.BaseExceptionHandler;
import com.veloProWeb.exceptions.data.LocalDataNotFoundException;
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
