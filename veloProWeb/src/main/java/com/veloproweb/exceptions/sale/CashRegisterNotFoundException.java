package com.veloproweb.exceptions.sale;

public class CashRegisterNotFoundException extends RuntimeException {
    public CashRegisterNotFoundException(String message) {
        super(message);
    }
}
