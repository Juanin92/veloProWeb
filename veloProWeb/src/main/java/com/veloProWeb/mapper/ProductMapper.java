package com.veloProWeb.mapper;

import com.veloProWeb.model.dto.product.ProductRequestDTO;
import com.veloProWeb.model.dto.product.ProductResponseDTO;
import com.veloProWeb.model.entity.product.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDTO dto){
        return Product.builder()
                .description(dto.getDescription())
                .brand(dto.getBrand())
                .subcategoryProduct(dto.getSubcategoryProduct())
                .category(dto.getCategory())
                .unit(dto.getUnit()).build();
    }

    public ProductResponseDTO toResponse(Product product){
        return ProductResponseDTO.builder()
                .id(product.getId())
                .description(product.getDescription())
                .salePrice(product.getSalePrice())
                .buyPrice(product.getBuyPrice())
                .stock(product.getStock())
                .reserve(product.getReserve())
                .threshold(product.getThreshold())
                .statusProduct(product.getStatusProduct())
                .brand(product.getBrand().getName())
                .unit(product.getUnit().getNameUnit())
                .subcategoryProduct(product.getSubcategoryProduct().getName())
                .category(product.getCategory().getName())
                .build();
    }
}
