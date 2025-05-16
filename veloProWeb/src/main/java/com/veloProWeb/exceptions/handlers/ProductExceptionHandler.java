package com.veloProWeb.exceptions.handlers;

import com.veloProWeb.exceptions.BaseExceptionHandler;
import com.veloProWeb.exceptions.product.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ProductExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler({
            BrandAlreadyExistsException.class,
            CategoryAlreadyExistsException.class,
            UnitAlreadyExistsException.class,
            SubcategoryAlreadyExistsException.class,
            ProductAlreadyDeletedException.class,
            ProductAlreadyActivatedException.class
    })
    public ResponseEntity<Map<String, String>> handleProductException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({CategoryNotFoundException.class, ProductNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleProductNotFound(CategoryNotFoundException e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
