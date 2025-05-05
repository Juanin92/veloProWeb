package com.veloProWeb.exceptions.Handlers;

import com.veloProWeb.exceptions.BaseExceptionHandler;
import com.veloProWeb.exceptions.Customer.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CustomerExceptionsHandler extends BaseExceptionHandler {

    @ExceptionHandler({
            CustomerAlreadyExistsException.class,
            CustomerAlreadyActivatedException.class,
            CustomerAlreadyDeletedException.class,
            InvalidPaymentAmountException.class
    })
    public ResponseEntity<Map<String, String>> handleCustomerException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCustomerNotFound(CustomerNotFoundException e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomerNotValidException.class)
    public ResponseEntity<Map<String, String>> handleCustomerNotValid(CustomerNotValidException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
