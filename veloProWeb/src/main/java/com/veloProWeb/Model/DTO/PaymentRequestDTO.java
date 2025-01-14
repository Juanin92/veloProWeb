package com.veloProWeb.Model.DTO;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import lombok.Data;

import java.util.List;

@Data
public class PaymentRequestDTO {
    private List<Long> ticketIDs;
    private Long customerID;
    private int amount;
    private String comment;
    private int totalPaymentPaid;
}
