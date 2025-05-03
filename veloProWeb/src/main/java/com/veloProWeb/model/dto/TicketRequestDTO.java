package com.veloProWeb.model.dto;

import com.veloProWeb.model.entity.customer.Customer;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketRequestDTO {
    private Customer customer;
    private Long number;
    private int total;
    private LocalDate date;
}
