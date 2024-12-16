package com.veloProWeb.Service.Customer;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import com.veloProWeb.Model.Enum.PaymentStatus;
import com.veloProWeb.Repository.Customer.TicketHistoryRepo;
import com.veloProWeb.Service.Customer.Interfaces.ICustomerService;
import com.veloProWeb.Service.Customer.Interfaces.ITicketHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TicketHistoryService implements ITicketHistoryService {

    @Autowired private TicketHistoryRepo ticketHistoryRepo;
    @Autowired private ICustomerService customerService;
    private LocalDate lastValidationDate;

    @Override
    public void AddTicketToCustomer(Customer customer, Long number, int total, LocalDate date) {
        TicketHistory ticket = new TicketHistory();
        String name = "BO00" + number;
        ticket.setTotal(total);
        ticket.setDocument(name);
        ticket.setStatus(false);
        ticket.setDate(date);
        ticket.setCustomer(customer);
        ticketHistoryRepo.save(ticket);
    }

    @Override
    public List<TicketHistory> getByCustomerId(Long id) {
        return ticketHistoryRepo.findByCustomerId(id);
    }

    @Override
    public void valideTicketByCustomer(Customer customer) {
        LocalDate today = LocalDate.now();
        if (lastValidationDate == null || !lastValidationDate.equals(today)) {
            List<TicketHistory> tickets = getByCustomerId(customer.getId());
            for (TicketHistory ticket : tickets) {
                if (!ticket.isStatus() && validateDate(ticket)) {
                    customer.setStatus(PaymentStatus.VENCIDA);
                    customerService.updateTotalDebt(customer);
                }
            }
            lastValidationDate = today;
        }
    }

    public boolean validateDate(TicketHistory ticket) {
        LocalDate now = LocalDate.now();
        LocalDate ticketDate = ticket.getDate();
        LocalDate notificationDate = ticket.getNotificationsDate();

        if (ticket.isStatus()){
            return false;
        }
        if (ChronoUnit.DAYS.between(ticketDate, now) > 30 &&
                (notificationDate == null || ChronoUnit.DAYS.between(notificationDate, now) > 15)) {
            ticket.setNotificationsDate(now);
            ticketHistoryRepo.save(ticket);
            return true;
        }
        return false;
    }

    @Override
    public void updateStatus(TicketHistory ticket) {
        ticket.setStatus(true);
        ticketHistoryRepo.save(ticket);
    }
}
