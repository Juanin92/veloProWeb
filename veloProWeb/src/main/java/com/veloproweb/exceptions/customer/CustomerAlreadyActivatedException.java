package com.veloproweb.exceptions.customer;

public class CustomerAlreadyActivatedException extends RuntimeException{
    public CustomerAlreadyActivatedException(String message){ super(message);}
}
