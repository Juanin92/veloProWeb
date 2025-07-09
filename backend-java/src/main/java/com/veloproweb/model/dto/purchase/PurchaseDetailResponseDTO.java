package com.veloproweb.model.dto.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDetailResponseDTO {
    private String descriptionProduct;
    private int price;
    private int tax;
    private int quantity;
    private int total;
}
