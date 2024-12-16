package com.veloProWeb.Model.DTO;

import com.veloProWeb.Model.Entity.Customer.Customer;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketRequestDTO {
    private Customer customer;
    private Long number;
    private int total;
    private LocalDate date;
}
