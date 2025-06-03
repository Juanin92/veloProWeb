package com.veloProWeb.model.dto.sale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailSaleDTO {
    private Long id;
    private Long idProduct;
    private int quantity;
}
