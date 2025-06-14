package com.veloproweb.exceptions.purchase;

public class PurchaseTotalMismatchException extends RuntimeException {
    public PurchaseTotalMismatchException(String message) {
        super(message);
    }
}
