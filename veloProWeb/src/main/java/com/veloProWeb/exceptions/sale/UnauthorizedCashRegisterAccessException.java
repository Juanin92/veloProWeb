package com.veloProWeb.exceptions.sale;

public class UnauthorizedCashRegisterAccessException extends RuntimeException {
    public UnauthorizedCashRegisterAccessException(String message) {
        super(message);
    }
}
