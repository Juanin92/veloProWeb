package com.veloProWeb.Model.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DetailSaleRequestDTO {
    private String descriptionProduct;
    private int quantity;
    private int price;
    private int tax;
    private String customer;
    private LocalDate notification;
    private boolean ticketStatus;
}
