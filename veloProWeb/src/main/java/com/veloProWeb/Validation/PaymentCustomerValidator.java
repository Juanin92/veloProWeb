package com.veloProWeb.Validation;

import org.springframework.stereotype.Component;

@Component
public class PaymentCustomerValidator {

    /**
     * Válida los datos de pago del cliente
     * @param amount - monto del pago
     * @param comment - comentario asociado al pago
     */
    public void validatePayment(String amount, String comment) {
        validatePaymentAmount(amount);
        validateComment(comment);
    }

    /**
     * Válida la cantidad del pago sea un número válido, si no lanza una excepción
     * si el monto no contiene solo dígitos en la cadena.
     * @param amount - cantidad del pago en forma de cadena
     */
    private void validatePaymentAmount(String amount) {
        if (!amount.matches("\\d+")) {
            throw new IllegalArgumentException("Ingrese solo números.");
        }
    }

    /**
     * Válida que un comentario no sea nulo o este vacía la cadena, si no lanza una excepción.
     * @param comment - cadena con el comentario.
     */
    private void validateComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Seleccione una forma de pago.");
        }
    }
}
