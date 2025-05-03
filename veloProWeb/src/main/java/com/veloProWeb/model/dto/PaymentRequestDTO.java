package com.veloProWeb.model.dto;

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
