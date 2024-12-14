package com.veloProWeb.Validation;

import org.springframework.stereotype.Component;

@Component
public class PaymentCustomerValidator {
    public void validatePayment(String amount, String comment) {
        validatePaymentAmount(amount);
        validateComment(comment);
    }

    private void validatePaymentAmount(String amount) {
        if (!amount.matches("\\d+")) {
            throw new IllegalArgumentException("Ingrese solo números.");
        }
    }
    private void validateComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Seleccione una forma de pago.");
        }
    }
}
