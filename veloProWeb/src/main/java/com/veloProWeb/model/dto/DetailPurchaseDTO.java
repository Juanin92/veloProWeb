package com.veloProWeb.model.dto;

import lombok.Data;

@Data
public class DetailPurchaseDTO {
    private Long id;
    private Long idProduct;
    private Long idPurchase;
    private int price;
    private int tax;
    private int quantity;
    private int total;
}
