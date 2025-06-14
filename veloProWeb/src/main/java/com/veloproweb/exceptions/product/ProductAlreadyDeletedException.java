package com.veloproweb.exceptions.product;

public class ProductAlreadyDeletedException extends RuntimeException{
    public ProductAlreadyDeletedException(String message){super(message);}
}
