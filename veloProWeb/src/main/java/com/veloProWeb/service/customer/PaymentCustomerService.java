package com.veloProWeb.service.customer;

import com.veloProWeb.exceptions.Customer.InvalidPaymentAmountException;
import com.veloProWeb.exceptions.Customer.NoTicketSelectedException;
import com.veloProWeb.model.dto.customer.PaymentRequestDTO;
import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.model.entity.customer.PaymentCustomer;
import com.veloProWeb.model.entity.customer.TicketHistory;
import com.veloProWeb.repository.customer.PaymentCustomerRepo;
import com.veloProWeb.service.customer.interfaces.IPaymentCustomerService;
import com.veloProWeb.validation.PaymentCustomerValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentCustomerService implements IPaymentCustomerService {

    private final PaymentCustomerRepo paymentCustomerRepo;
    private final PaymentCustomerValidator validator;
    private final TicketHistoryService ticketService;
    private final CustomerService customerService;

    /**
     * Procesa la creación de un pago basado en los valores del dto.
     * Gestiona el proceso de pago para uno o varios tickets.
     * Si el pago cubre una parte de la deuda, se genera un ajuste en el sistema.
     * @param dto - Contiene los detalles del pago
     */
    @Override
    public void createPaymentProcess(PaymentRequestDTO dto) {
        Customer customer = customerService.getCustomerById(dto.getCustomerID());
        List<TicketHistory> ticketList = new java.util.ArrayList<>(List.of());
        for (Long id : dto.getTicketIDs()){
            TicketHistory ticketHistory = ticketService.getTicketByID(id);
            ticketList.add(ticketHistory);
        }
        if (!ticketList.isEmpty()){
            int totalSelectedTicket = ticketList.stream().mapToInt(TicketHistory::getTotal).sum();
            if (ticketList.size() > 1){
                if (dto.getAmount() == totalSelectedTicket){
                    for (TicketHistory ticket : ticketList){
                        paymentDebtCustomer(ticket, dto.getComment(), dto.getAmount(), true);
                        ticketService.updateStatus(ticket);
                        dto.setAmount(dto.getAmount() - ticket.getTotal());
                    }
                }else {
                    throw new InvalidPaymentAmountException("El monto no es correcto para el pago de la deuda");
                }
            }else {
                if (dto.getAmount() == (ticketList.getFirst().getTotal() - dto.getTotalPaymentPaid())){
                    paymentDebtCustomer(ticketList.getFirst(), dto.getComment(), dto.getAmount(), false);
                    ticketService.updateStatus(ticketList.getFirst());
                } else if (dto.getAmount() < (ticketList.getFirst().getTotal() - dto.getTotalPaymentPaid())) {
                    createAdjustPayments(dto.getAmount(), ticketList.getFirst(), customer);
                    customerService.paymentDebt(customer, String.valueOf(dto.getAmount()));
                }else {
                    throw new InvalidPaymentAmountException("El monto supera el valor de la deuda.");
                }
            }
        }else {
            throw new NoTicketSelectedException("No ha seleccionado ninguna boleta");
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
        List<TicketHistory> tickets = ticketService.getByCustomerId(idCustomer);

        return payments.stream()
                .filter(payment -> tickets.stream()
                        .anyMatch(ticket -> Objects.equals(ticket.getId(), payment.getDocument().getId())))
                .sorted(Comparator.comparing(PaymentCustomer::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Agrega un pago a la deuda de un cliente.
     * Se encarga de válidar el pago y asignar los detalles antes de registrar el pago
     * @param paymentCustomer contiene el detalle del pago
     */
    private void addPayments(PaymentCustomer paymentCustomer) {
        validator.validatePayment(String.valueOf(paymentCustomer.getAmount()),paymentCustomer.getComment());
        paymentCustomer.setCustomer(paymentCustomer.getCustomer());
        paymentCustomer.setDate(LocalDate.now());
        paymentCustomer.setDocument(paymentCustomer.getDocument());
        paymentCustomer.setAmount(paymentCustomer.getAmount());
        paymentCustomer.setComment(paymentCustomer.getComment());
        paymentCustomerRepo.save(paymentCustomer);
    }

    /**
     * Agrega un ajuste (diferencia) a la deuda del cliente
     * @param amount valor abonado
     * @param ticket ticket al que se asocia el monto abonado
     * @param customer cliente al cual se le hace el ajuste
     */
    private void createAdjustPayments(int amount, TicketHistory ticket, Customer customer) {
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
     * Creación de Pago de cliente
     * procesa la reducción de la deuda del cliente, utilizando el monto especificado o
     * el total del ticket según si el pago corresponde a una lista de tickets o no.
     * @param ticket - Ticket que se realiza el pago
     * @param comment - cadena con comentario
     * @param amount - monto a pagar
     * @param list - Indica si el pago desde una lista de tickets o no
     */
    private void paymentDebtCustomer(TicketHistory ticket, String comment, int amount, boolean list){
        PaymentCustomer paymentCustomer = new PaymentCustomer();
        paymentCustomer.setCustomer(ticket.getCustomer());
        paymentCustomer.setDocument(ticket);
        paymentCustomer.setComment(comment);
        paymentCustomer.setAmount(amount);
        addPayments(paymentCustomer);
        customerService.paymentDebt(ticket.getCustomer(), list ? String.valueOf(ticket.getTotal()) : String.valueOf(amount));
    }
}
