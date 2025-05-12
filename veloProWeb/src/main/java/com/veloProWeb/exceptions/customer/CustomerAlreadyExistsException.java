package com.veloProWeb.exceptions.customer;

public class CustomerAlreadyExistsException extends RuntimeException{
    public CustomerAlreadyExistsException(String message){super(message);}
}
