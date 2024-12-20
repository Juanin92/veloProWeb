package com.veloProWeb.Validation;

import com.veloProWeb.Model.Entity.Product.*;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    public void validateNewProduct(Product product){
        validateBrand(product.getBrand());
        validateCategory(product.getCategory());
        validateSubcategory(product.getSubcategoryProduct());
        validateUnit(product.getUnit());
        validateDescription(product.getDescription());
    }

    private void validateBrand(BrandProduct brand){
        if (brand == null){
            throw new IllegalArgumentException("Seleccione una marca");
        }
    }
    private  void validateCategory(CategoryProduct category){
        if (category == null){
            throw new IllegalArgumentException("Seleccione una categoría");
        }
    }
    private void validateSubcategory(SubcategoryProduct subcategory){
        if (subcategory == null){
            throw new IllegalArgumentException("Seleccione una subcategoría");
        }
    }
    private void validateUnit(UnitProduct unit){
        if (unit == null){
            throw new IllegalArgumentException("Seleccione una unidad");
        }
    }
    private void validateDescription(String description){
        if (description == null || description.trim().isBlank()){
            throw new IllegalArgumentException("Ingrese una descripción");
        }
    }
    private void validateStock(int stock){
        if (stock >= 0){
            throw new IllegalArgumentException("Cantidad de ser mayor a 0");
        }
    }
    private void validateSalePrice(int salePrice){
        if (salePrice >= 0){
            throw new IllegalArgumentException("Precio de ser mayor a 0");
        }
    }
}
