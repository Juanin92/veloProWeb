package com.veloProWeb.exceptions.customer;

public class InvalidPaymentAmountException extends RuntimeException{
    public InvalidPaymentAmountException(String message){ super(message);}
}
