package com.veloProWeb.exceptions.handlers;

import com.veloProWeb.exceptions.BaseExceptionHandler;
import com.veloProWeb.exceptions.sale.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class SaleExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler({
            CashRegisterNotFoundException.class,
            DispatchNotFoundException.class,
            SaleNotFoundException.class
    })
    public ResponseEntity<Map<String, String>> handleSaleNotFoundException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            CashRegisterDateNotMatchException.class,
            InvalidAmountCashRegisterException.class,
            InvalidDispatchStatusException.class
    })
    public ResponseEntity<Map<String, String>> handleSaleException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedCashRegisterAccessException.class)
    public ResponseEntity<Map<String, String>> handleSaleUnauthorizedException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
