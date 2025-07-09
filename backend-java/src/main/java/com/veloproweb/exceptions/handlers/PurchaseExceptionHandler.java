package com.veloproweb.exceptions.handlers;

import com.veloproweb.exceptions.BaseExceptionHandler;
import com.veloproweb.exceptions.purchase.PurchaseNotFoundException;
import com.veloproweb.exceptions.purchase.PurchaseTotalMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class PurchaseExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(PurchaseNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePurchaseNotFound(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PurchaseTotalMismatchException.class)
    public ResponseEntity<Map<String, String>> handlePurchaseException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }
}
