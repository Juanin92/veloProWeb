package com.veloProWeb.model.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdatedRequestDTO {

    private Long id;
    private String description;
    private int salePrice;
    private int stock;
}
