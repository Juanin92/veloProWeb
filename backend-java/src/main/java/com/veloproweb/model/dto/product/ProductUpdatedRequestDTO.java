package com.veloproweb.model.dto.product;

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
public class ProductUpdatedRequestDTO {

    @NotNull(message = "El ID es obligatorio")
    private Long id;

    @NotBlank(message = "La descripción del producto es obligatoria")
    private String description;

    @NotNull(message = "El precio de compra es obligatorio")
    private int salePrice;

    @NotNull(message = "La cantidad de stock es obligatorio")
    private int stock;

    private String comment;
}
