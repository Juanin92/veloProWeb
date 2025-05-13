package com.veloProWeb.exceptions.handlers;

import com.veloProWeb.exceptions.BaseExceptionHandler;
import com.veloProWeb.exceptions.product.BrandAlreadyExistsException;
import com.veloProWeb.exceptions.product.CategoryAlreadyExistsException;
import com.veloProWeb.exceptions.product.SubcategoryAlreadyExistsException;
import com.veloProWeb.exceptions.product.UnitAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

public class ProductExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler({
            BrandAlreadyExistsException.class,
            CategoryAlreadyExistsException.class,
            UnitAlreadyExistsException.class,
            SubcategoryAlreadyExistsException.class
    })
    public ResponseEntity<Map<String, String>> handleCustomerException(Exception e){
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }
}
