package com.veloProWeb.service.customer.interfaces;

import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.model.entity.customer.TicketHistory;

import java.time.LocalDate;
import java.util.List;

public interface ITicketHistoryService {

    void AddTicketToCustomer(Customer customer, Long number, int total, LocalDate date);
    List<TicketHistory> getByCustomerId(Long id);
    void updateStatus(TicketHistory  ticket);
    TicketHistory getTicketByID(Long Id);
}
