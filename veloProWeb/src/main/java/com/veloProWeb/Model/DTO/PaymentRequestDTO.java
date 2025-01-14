package com.veloProWeb.Model.DTO;

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
