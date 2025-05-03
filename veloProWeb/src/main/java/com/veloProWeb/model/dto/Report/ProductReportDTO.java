package com.veloProWeb.model.dto.Report;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductReportDTO {
    private Long id;
    private String brand;
    private String description;
    private String categoryName;
    private int total;

    public ProductReportDTO(Long id, String brand, String description, String categoryName, BigDecimal total) {
        this.id = id;
        this.brand = brand;
        this.description = description;
        this.categoryName = categoryName;
        this.total = total.intValue();
    }
}
