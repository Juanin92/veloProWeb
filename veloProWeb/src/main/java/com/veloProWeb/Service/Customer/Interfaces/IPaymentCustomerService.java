package com.veloProWeb.Service.Customer.Interfaces;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.PaymentCustomer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;

import java.util.List;

public interface IPaymentCustomerService {
    void addPayments(PaymentCustomer customer);
    void createAdjustPayments(int amount, TicketHistory ticket, Customer customer);
    List<PaymentCustomer> getAll();
    List<PaymentCustomer> getCustomerSelected(Long id);
    int calculateDebtTicket(TicketHistory ticket);
}
