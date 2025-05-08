package com.veloProWeb.scheduler;

import com.veloProWeb.model.Enum.PaymentStatus;
import com.veloProWeb.model.dto.customer.CustomerResponseDTO;
import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.model.entity.customer.TicketHistory;
import com.veloProWeb.service.customer.interfaces.ICustomerService;
import com.veloProWeb.service.customer.interfaces.ITicketHistoryService;
import com.veloProWeb.util.EmailService;
import lombok.RequiredArgsConstructor;
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

    private boolean shouldNotify(TicketHistory ticket) {
        LocalDate now = LocalDate.now();
        LocalDate ticketDate = ticket.getDate();
        LocalDate notificationDate = ticket.getNotificationsDate();

        return ChronoUnit.DAYS.between(ticketDate, now) > 30 &&
                (notificationDate == null || ChronoUnit.DAYS.between(notificationDate, now) > 15);
    }
}
