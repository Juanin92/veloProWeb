package com.veloProWeb.exceptions.customer;

public class CustomerAlreadyDeletedException extends RuntimeException{
    public CustomerAlreadyDeletedException(String message){ super(message);}
}
