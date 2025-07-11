package com.veloproweb.exceptions.handlers;

import com.veloproweb.exceptions.BaseExceptionHandler;
import com.veloproweb.exceptions.customer.*;
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
            InvalidPaymentAmountException.class,
            NoTicketSelectedException.class,
            TicketAlreadyPaidException.class
    })
    public ResponseEntity<Map<String, String>> handleCustomerException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({CustomerNotFoundException.class, TicketNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleCustomerNotFound(Exception e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomerNotValidException.class)
    public ResponseEntity<Map<String, String>> handleCustomerNotValid(CustomerNotValidException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
