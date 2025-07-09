package com.veloproweb.model.dto.sale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleDetailResponseDTO {
    private Long idProduct;
    private String descriptionProduct;
    private int quantity;
    private int price;
    private int tax;
    private boolean hasDispatch;
}
