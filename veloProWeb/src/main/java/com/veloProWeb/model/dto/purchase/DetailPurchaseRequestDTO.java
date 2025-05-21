package com.veloProWeb.model.dto.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailPurchaseRequestDTO {
    private Long idProduct;
    private Long idPurchase;
    private int price;
    private int tax;
    private int quantity;
    private int total;
}
