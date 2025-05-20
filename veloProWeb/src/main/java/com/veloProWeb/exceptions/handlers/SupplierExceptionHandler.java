package com.veloProWeb.exceptions.handlers;

import com.veloProWeb.exceptions.BaseExceptionHandler;
import com.veloProWeb.exceptions.supplier.SupplierAlreadyExistsException;
import com.veloProWeb.exceptions.supplier.SupplierNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class SupplierExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(SupplierAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleSupplierException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SupplierNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleSupplierNotFound(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
