package com.veloProWeb.Service.Customer.Interfaces;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;

import java.time.LocalDate;
import java.util.List;

public interface ITicketHistoryService {

    void AddTicketToCustomer(Customer customer, Long number, int total, LocalDate date);
    List<TicketHistory> getAll();
    List<TicketHistory> getByCustomerId(Long id);
    void valideTicketByCustomer(Customer customer);
    void updateStatus(TicketHistory  ticket);
}
