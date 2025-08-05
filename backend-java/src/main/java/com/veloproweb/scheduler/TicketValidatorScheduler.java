package com.veloproweb.scheduler;

import com.veloproweb.model.enums.PaymentStatus;
import com.veloproweb.model.dto.customer.CustomerResponseDTO;
import com.veloproweb.model.entity.customer.Customer;
import com.veloproweb.model.entity.customer.TicketHistory;
import com.veloproweb.service.customer.interfaces.ICustomerService;
import com.veloproweb.service.customer.interfaces.ITicketHistoryService;
import com.veloproweb.util.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketValidatorScheduler {

    private final ITicketHistoryService ticketHistoryService;
    private final ICustomerService customerService;
    private final EmailService emailService;

    private LocalDate lastValidationDate;

    /**
     * Válida los tickets de todos los clientes diariamente a las 12:00 PM.
     * Si la fecha de validación es diferente a la fecha actual, se valida cada ticket
     * y se envía un correo electrónico al cliente si el ticket está vencido.
     */
    @Scheduled(cron = "0 0 12 * * ?") // Cada día a las 12:00 PM
    @Async("taskExecutor")
    public void validateTicketsDaily() {
        LocalDate today = LocalDate.now();
        if (lastValidationDate == null || !lastValidationDate.equals(today)) {
            List<CustomerResponseDTO> customers = customerService.getAll();
            for (CustomerResponseDTO customerResponse : customers) {
                Customer customer = customerService.getCustomerById(customerResponse.getId());
                List<TicketHistory> tickets = ticketHistoryService.getByCustomerId(customerResponse.getId());
                for (TicketHistory ticket : tickets) {
                    if (!ticket.isStatus() && shouldNotify(ticket)) {
                        customer.setStatus(PaymentStatus.VENCIDA);
                        customerService.updateTotalDebt(customer);
                        emailService.sendOverdueTicketNotification(customer, ticket);
                    }
                }
            }
            lastValidationDate = today;
        }
    }

    /**
     * Válida la fecha de un ticket y actualiza la fecha de notificación si es necesario.
     * Verifica si el estado del ticket es falso y si han pasado más de 30 días desde la fecha del ticket.
     * También verifica si la fecha de notificación es nula o si han pasado más de 15 días desde la fecha de última notificación.
     * Si se cumplen estas condiciones, actualiza la fecha de notificación del ticket a la fecha actual y guarda el ticket en el repositorio.
     *
     * @param ticket ticket que se debe validar
     * @return verdadero si el ticket fue validado y la fecha de notificación fue actualizada, falso en caso contrario
     */
    private boolean shouldNotify(TicketHistory ticket) {
        LocalDate now = LocalDate.now();
        LocalDate ticketDate = ticket.getDate();
        LocalDate notificationDate = ticket.getNotificationsDate();

        return ChronoUnit.DAYS.between(ticketDate, now) > 30 &&
                (notificationDate == null || ChronoUnit.DAYS.between(notificationDate, now) > 15);
    }
}
