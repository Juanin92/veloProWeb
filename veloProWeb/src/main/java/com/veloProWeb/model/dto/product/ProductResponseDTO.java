package com.veloProWeb.model.dto.product;

import com.veloProWeb.model.Enum.StatusProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String description;
    private int salePrice;
    private int buyPrice;
    private int stock;
    private int reserve;
    private int threshold;
    private StatusProduct statusProduct;
    private String brand;
    private String unit;
    private String subcategoryProduct;
    private String category;
}
