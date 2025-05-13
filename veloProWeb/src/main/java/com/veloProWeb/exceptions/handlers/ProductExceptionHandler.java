package com.veloProWeb.exceptions.handlers;

import com.veloProWeb.exceptions.BaseExceptionHandler;
import com.veloProWeb.exceptions.customer.CustomerNotFoundException;
import com.veloProWeb.exceptions.customer.TicketNotFoundException;
import com.veloProWeb.exceptions.product.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

public class ProductExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler({
            BrandAlreadyExistsException.class,
            CategoryAlreadyExistsException.class,
            UnitAlreadyExistsException.class,
            SubcategoryAlreadyExistsException.class,
            ProductAlreadyDeletedException.class,
            ProductAlreadyActivatedException.class
    })
    public ResponseEntity<Map<String, String>> handleCustomerException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({CategoryNotFoundException.class, ProductNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleCustomerNotFound(CategoryNotFoundException e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
