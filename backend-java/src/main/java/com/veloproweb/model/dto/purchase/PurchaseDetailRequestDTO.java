package com.veloproweb.model.dto.purchase;

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
public class PurchaseDetailRequestDTO {

    @NotNull(message = "Identificador del producto es obligatorio")
    private Long idProduct;
    private Long idPurchase;

    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser un número positivo")
    @Min(value = 1, message = "El precio unitario debe ser mayor a 0")
    private int price;

    @NotNull(message = "El impuesto es obligatorio")
    @Positive(message = "El impuesto debe ser un número positivo")
    @Min(value = 1, message = "El impuesto debe ser mayor a 0")
    private int tax;

    @NotNull(message = "La cantidad de producto es obligatorio")
    @Positive(message = "La cantidad de producto debe ser un número positivo")
    @Min(value = 1, message = "La cantidad de producto debe ser mayor a 0")
    private int quantity;

    @NotNull(message = "El total de la compra del producto es obligatorio")
    @Positive(message = "El total de la compra del producto debe ser un número positivo")
    @Min(value = 1, message = "El total de la compra del producto debe ser mayor a 0")
    private int total;
}
