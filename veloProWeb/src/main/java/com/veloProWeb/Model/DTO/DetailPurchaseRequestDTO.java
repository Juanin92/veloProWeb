package com.veloProWeb.Model.DTO;

import lombok.Data;

@Data
public class DetailPurchaseRequestDTO {
    private String descriptionProduct;
    private int price;
    private int tax;
    private int quantity;
    private int total;
}
