package com.veloProWeb.exceptions.Customer;

public class InvalidPaymentAmountException extends RuntimeException{
    public InvalidPaymentAmountException(String message){ super(message);}
}
