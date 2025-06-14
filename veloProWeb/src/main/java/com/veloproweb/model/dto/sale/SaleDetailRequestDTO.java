package com.veloproweb.model.dto.sale;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleDetailRequestDTO {

    @NotNull(message = "Identificador del producto es obligatorio")
    private Long idProduct;

    @NotNull(message = "La cantidad de producto es obligatorio")
    @Positive(message = "La cantidad de producto debe ser un número positivo")
    @Min(value = 1, message = "La cantidad de producto debe ser mayor a 0")
    private int quantity;
}
