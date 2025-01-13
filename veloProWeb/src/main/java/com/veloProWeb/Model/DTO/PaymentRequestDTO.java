package com.veloProWeb.Model.DTO;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import lombok.Data;

import java.util.List;

@Data
public class PaymentRequestDTO {
    private List<TicketHistory> tickets;
    private Customer customer;
    private int amount;
    private String comment;
}
