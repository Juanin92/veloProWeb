package com.veloproweb.service.sale;

import com.veloproweb.model.enums.PaymentMethod;
import com.veloproweb.model.entity.customer.Customer;
import com.veloproweb.service.customer.interfaces.ICustomerService;
import com.veloproweb.service.customer.interfaces.ITicketHistoryService;
import com.veloproweb.service.sale.interfaces.ISaleEventService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SaleEventService implements ISaleEventService {

    private final ICustomerService customerService;
    private final ITicketHistoryService ticketHistoryService;


    /**
     * Crea los eventos de venta con método de pago préstamo.
     *
     * @param customer - Cliente que hace el pago.
     * @param amount   - Monto del pago realizado.
     */
    @Override
    public void createSaleLoanPaymentEvent(Customer customer, int amount) {
        processPayment(customer, amount);
    }

    /**
     * Crea los eventos de venta con método de pago mixto.
     *
     * @param customer - Cliente que hace el pago.
     * @param amount   - Monto del pago realizado.
     */
    @Override
    public void createSaleMixPaymentEvent(Customer customer, int amount) {
        processPayment(customer, amount);
    }

    /**
     * Verifica si se necesita un cliente para el método de pago.
     * @param paymentMethod - Método de pago utilizado en la venta.
     * @param idCustomer - Identificador del cliente.
     * @return - Devuelve el cliente o de lo contrario devuelve null.
     */
    @Override
    public Customer needsCustomer(PaymentMethod paymentMethod, Long idCustomer) {
        if (paymentMethod == PaymentMethod.PRESTAMO || paymentMethod == PaymentMethod.MIXTO) {
            return customerService.getCustomerById(idCustomer);
        }
        return  null;
    }

    /**
     * Procesa el pago de un cliente.
     * Este método actualiza la deuda del cliente, agrega la venta al cliente
     * @param customer - Cliente que realiza el pago
     * @param amount - Monto del pago realizado
     */
    private void processPayment(Customer customer, int amount){
        customer.setTotalDebt(customer.getDebt() + amount);
        customerService.updateTotalDebt(customer);
        customerService.addSaleToCustomer(customer);
        ticketHistoryService.addTicketToCustomer(customer, amount);
    }
}
