package com.veloProWeb.exceptions.handlers;

import com.veloProWeb.exceptions.BaseExceptionHandler;
import com.veloProWeb.exceptions.sale.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CashRegisterExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler({CashRegisterNotFoundException.class, DispatchNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleCashRegisterNotFoundException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            CashRegisterDateNotMatchException.class,
            InvalidAmountCashRegisterException.class,
            InvalidDispatchStatusException.class
    })
    public ResponseEntity<Map<String, String>> handleCashRegisterException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedCashRegisterAccessException.class)
    public ResponseEntity<Map<String, String>> handleCashRegisterUnauthorizedException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
