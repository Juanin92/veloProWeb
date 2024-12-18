package com.veloProWeb.Service.Customer;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.PaymentCustomer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import com.veloProWeb.Repository.Customer.PaymentCustomerRepo;
import com.veloProWeb.Service.Customer.Interfaces.IPaymentCustomerService;
import com.veloProWeb.Validation.PaymentCustomerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PaymentCustomerService implements IPaymentCustomerService {

    @Autowired private PaymentCustomerRepo paymentCustomerRepo;
    @Autowired private PaymentCustomerValidator validator;
    @Autowired private TicketHistoryService ticketHistoryService;

    /**
     * Agrega un pago a la deuda de un cliente.
     * Se encarga de válidar el pago y asignar los detalles antes de registrar el pago
     * @param paymentCustomer contiene el detalle del pago
     */
    @Override
    public void addPayments(PaymentCustomer paymentCustomer) {
        validator.validatePayment(String.valueOf(paymentCustomer.getAmount()),paymentCustomer.getComment());
        paymentCustomer.setCustomer(paymentCustomer.getCustomer());
        paymentCustomer.setDate(LocalDate.now());
        paymentCustomer.setDocument(paymentCustomer.getDocument());
        paymentCustomer.setAmount(paymentCustomer.getAmount());
        paymentCustomer.setComment(paymentCustomer.getComment());
        paymentCustomerRepo.save(paymentCustomer);
    }

    /**
     * agrega un ajuste a la deuda del cliente
     * @param amount valor abonado
     * @param ticket ticket al que se asocia el monto abonado
     * @param customer cliente al cual se le hace el ajuste
     */
    @Override
    public void createAdjustPayments(int amount, TicketHistory ticket, Customer customer) {
        if (amount > 0) {
            PaymentCustomer paymentCustomer = new PaymentCustomer();
            paymentCustomer.setCustomer(customer);
            paymentCustomer.setDocument(ticket);
            paymentCustomer.setAmount(amount);
            paymentCustomer.setComment("Ajuste");
            addPayments(paymentCustomer);
        }
    }

    /**
     * Obtiene los registro de pagos del cliente
     * @return una lista con los registros de pagos.
     */
    @Override
    public List<PaymentCustomer> getAll() {
        return paymentCustomerRepo.findAll();
    }

    /**
     * Obtiene los pagos realizados de un cliente por su ID
     * filtrando que los pagos sean similares a un ticket y ordenando por fecha menor a mayor
     * y solo dejar una lista filtrada con los tickets que tenga pagos
     * @param idCustomer ID del cliente
     * @return lista de registro de pagos realizados
     */
    @Override
    public List<PaymentCustomer> getCustomerSelected(Long  idCustomer) {
        List<PaymentCustomer> payments =  paymentCustomerRepo.findByCustomerId(idCustomer);
        List<TicketHistory> tickets = ticketHistoryService.getByCustomerId(idCustomer);

        return payments.stream()
                .filter(payment -> tickets.stream()
                        .anyMatch(ticket -> Objects.equals(ticket.getId(), payment.getDocument().getId())))
                .sorted(Comparator.comparing(PaymentCustomer::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Calcula el monto a pagar por tickets seleccionados
     * @param ticket es el seleccionado para obtener el monto a pagar
     * @return la suma de los monto adeudos por ticket
     */
    @Override
    public int calculateDebtTicket(TicketHistory ticket) {
        List<PaymentCustomer> paymentTickets = paymentCustomerRepo.findByDocument(ticket);
        return paymentTickets.stream()
                .mapToInt(PaymentCustomer::getAmount)
                .sum();
    }
}
