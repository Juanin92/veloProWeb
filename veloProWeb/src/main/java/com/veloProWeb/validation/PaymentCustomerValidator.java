package com.veloProWeb.validation;

import com.veloProWeb.exceptions.customer.InvalidPaymentAmountException;
import com.veloProWeb.exceptions.customer.NoTicketSelectedException;
import com.veloProWeb.exceptions.validation.ValidationException;
import com.veloProWeb.model.entity.customer.TicketHistory;
import org.springframework.stereotype.Component;

import java.util.List;

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
     * Válida que la lista de boletas no esté vacía.
     * @param tickets - lista de boletas seleccionadas
     */
    public void validateTickets(List<TicketHistory> tickets){
        if (tickets.isEmpty()) {
            throw new NoTicketSelectedException("No ha seleccionado ninguna boleta");
        }
    }

    /**
     * Válida que el monto del pago sea igual al total de las boletas seleccionadas.
     * @param amount - monto del pago
     * @param tickets - boletas seleccionadas
     */
    public void validateExactPaymentForMultipleTickets(int amount, List<TicketHistory> tickets){
        int totalSelectedTicket = tickets.stream().mapToInt(TicketHistory::getTotal).sum();
        if (amount != totalSelectedTicket) {
            throw new InvalidPaymentAmountException("El monto no es correcto para el pago de la deuda");
        }
    }

    /**
     * Válida que el monto del pago no exceda la deuda.
     * @param amount - monto de la deuda
     * @param paymentDebt - monto del pago
     */
    public void validatePaymentNotExceedDebt(int amount, int paymentDebt){
        if (amount > paymentDebt) {
            throw new InvalidPaymentAmountException("El monto supera el valor de la deuda.");
        }
    }

    /**
     * Válida la cantidad del pago sea un número válido, si no lanza una excepción
     * si el monto no contiene solo dígitos en la cadena.
     * @param amount - cantidad del pago en forma de cadena
     */
    private void validatePaymentAmount(String amount) {
        if (!amount.matches("\\d+")) {
            throw new ValidationException("Ingrese solo números.");
        }
    }

    /**
     * Válida que un comentario no sea nulo o este vacía la cadena, si no lanza una excepción.
     * @param comment - cadena con el comentario.
     */
    private void validateComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new ValidationException("Seleccione una forma de pago.");
        }
    }
}
