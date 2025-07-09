package com.veloproweb.model.dto.product;

import com.veloproweb.model.entity.product.BrandProduct;
import com.veloproweb.model.entity.product.CategoryProduct;
import com.veloproweb.model.entity.product.SubcategoryProduct;
import com.veloproweb.model.entity.product.UnitProduct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

    @NotBlank(message = "El campo descripción no puede estar vacío")
    private String description;

    @NotNull(message = "Debe seleccionar una marca")
    private BrandProduct brand;

    @NotNull(message = "Debe seleccionar una unidad de medida")
    private UnitProduct unit;

    @NotNull(message = "Debe seleccionar una categoría")
    private CategoryProduct category;

    @NotNull(message = "Debe seleccionar una subcategoría")
    private SubcategoryProduct subcategoryProduct;
}
