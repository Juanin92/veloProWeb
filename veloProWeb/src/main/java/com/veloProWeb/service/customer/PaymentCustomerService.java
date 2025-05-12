package com.veloProWeb.service.customer;

import com.veloProWeb.mapper.PaymentCustomerMapper;
import com.veloProWeb.model.dto.customer.PaymentRequestDTO;
import com.veloProWeb.model.dto.customer.PaymentResponseDTO;
import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.model.entity.customer.PaymentCustomer;
import com.veloProWeb.model.entity.customer.TicketHistory;
import com.veloProWeb.repository.customer.PaymentCustomerRepo;
import com.veloProWeb.service.customer.interfaces.IPaymentCustomerService;
import com.veloProWeb.validation.PaymentCustomerValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentCustomerService implements IPaymentCustomerService {

    private final PaymentCustomerRepo paymentCustomerRepo;
    private final PaymentCustomerValidator validator;
    private final TicketHistoryService ticketService;
    private final CustomerService customerService;
    private final PaymentCustomerMapper mapper;

    /**
     * Obtiene los registro de pagos del cliente.
     * @return una lista con los registros de pagos.
     */
    @Override
    public List<PaymentResponseDTO> getAll() {
        return paymentCustomerRepo.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Procesa la creación de un pago.
     * Gestiona el proceso de pago para uno o varios tickets.
     * @param dto - Contiene los detalles del pago
     */
    @Transactional
    @Override
    public void createPaymentProcess(PaymentRequestDTO dto) {
        List<TicketHistory> ticketList = loadTicketHistories(dto.getTicketIDs());
        validator.validateTickets(ticketList);
        if (ticketList.size() > 1){
            processMultipleTicketPayment(dto, ticketList);
        }else {
            processSingleTicketPayment(dto, ticketList.getFirst());
        }
    }

    /**
     * Obtiene los pagos realizados de un cliente por su ID
     * filtrando que los pagos sean similares a un ticket y ordenando por fecha menor a mayor
     * y solo dejar una lista filtrada con los tickets que tenga pagos
     *
     * @param idCustomer ID del cliente
     * @return lista de registro de pagos realizados
     */
    @Override
    public List<PaymentResponseDTO> getCustomerSelected(Long idCustomer) {
        Set<Long> ticketIds = ticketService.getByCustomerId(idCustomer).stream()
                .map(TicketHistory::getId)
                .collect(Collectors.toSet());

        return paymentCustomerRepo.findByCustomerId(idCustomer).stream()
                .filter(payment -> ticketIds.contains(payment.getDocument().getId()))
                .sorted(Comparator.comparing(PaymentCustomer::getDate))
                .map(mapper::toDto)
                .toList();

    }

    private List<TicketHistory> loadTicketHistories(List<Long> ticketIds) {
        return ticketIds.stream()
                .map(ticketService::getTicketById)
                .toList();
    }

    private void processMultipleTicketPayment(PaymentRequestDTO dto, List<TicketHistory> tickets){
        int remainingAmount = dto.getAmount();
        validator.validateExactPaymentForMultipleTickets(remainingAmount, tickets);

        for (TicketHistory ticket : tickets) {
            int ticketTotal = ticket.getTotal();
            paymentDebtCustomer(ticket, dto.getComment(), ticketTotal, true);
            ticketService.updateStatus(ticket);
            remainingAmount -= ticketTotal;
        }
    }

    private void processSingleTicketPayment(PaymentRequestDTO dto, TicketHistory ticket){
        Customer customer = customerService.getCustomerById(dto.getCustomerID());
        int paymentDebt = ticket.getTotal() - dto.getTotalPaymentPaid();
        validator.validatePaymentNotExceedDebt(dto.getAmount(), paymentDebt);
        if (dto.getAmount() == paymentDebt){
            paymentDebtCustomer(ticket, dto.getComment(), dto.getAmount(), false);
            ticketService.updateStatus(ticket);
        } else {
            createAdjustPayments(dto.getAmount(), ticket, customer);
            customerService.paymentDebt(customer, String.valueOf(dto.getAmount()));
        }
    }

    /**
     * Agrega un ajuste (diferencia) a la deuda del cliente
     * @param amount valor abonado
     * @param ticket ticket al que se asocia el monto abonado
     * @param customer cliente al cual se le hace el ajuste
     */
    private void createAdjustPayments(int amount, TicketHistory ticket, Customer customer) {
        if (amount > 0) {
            PaymentCustomer payment = PaymentCustomer.builder()
                    .customer(customer)
                    .document(ticket)
                    .amount(amount)
                    .comment("Ajuste")
                    .date(LocalDate.now())
                    .build();
            addPayments(payment);
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
        PaymentCustomer payment = PaymentCustomer.builder()
                .customer(ticket.getCustomer())
                .document(ticket)
                .amount(amount)
                .comment(comment)
                .date(LocalDate.now())
                .build();
        addPayments(payment);
        customerService.paymentDebt(ticket.getCustomer(),
                list ? String.valueOf(ticket.getTotal()) : String.valueOf(amount));
    }

    /**
     * Agrega un pago a la deuda de un cliente.
     * Se encarga de válidar el pago y asignar los detalles antes de registrar el pago
     * @param paymentCustomer contiene el detalle del pago
     */
    private void addPayments(PaymentCustomer paymentCustomer) {
        validator.validatePayment(String.valueOf(paymentCustomer.getAmount()),paymentCustomer.getComment());
        paymentCustomerRepo.save(paymentCustomer);
    }
}
