package com.veloProWeb.Model.DTO;

import lombok.Data;

@Data
public class DetailSaleDTO {
    private Long id;
    private String description;
    private String category;
    private String unit;
    private int stock;
    private int salePrice;
    private int tax;
    private int total;
    private int quantity;
}
