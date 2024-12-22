package com.veloProWeb.Model.DTO;

import com.veloProWeb.Model.Enum.StatusProduct;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String description;
    private int salePrice;
    private int buyPrice;
    private int stock;
    private boolean status;
    private StatusProduct statusProduct;
    private String brand;
    private String unit;
    private String subcategoryProduct;
    private String category;
}
