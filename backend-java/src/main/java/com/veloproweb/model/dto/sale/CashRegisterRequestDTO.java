package com.veloproweb.model.dto.sale;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class CashRegisterRequestDTO {

    @NotNull(message = "El ID de la caja es obligatorio")
    private Long id;

    @NotNull(message = "El monto de apertura de caja no puede estar vacío")
    @Positive(message = "El monto de apertura de caja debe ser positivo")
    @Min(value = 1, message = "El monto de apertura de caja debe ser mayor a 0")
    private int amountOpening;

    @NotNull(message = "El monto de cierre de caja no puede estar vacío")
    @Positive(message = "El monto de cierre de caja debe ser positivo")
    @Min(value = 1, message = "El monto de cierre de caja debe ser mayor a 0")
    private int amountClosingCash;

    @NotNull(message = "El monto del terminal POS no puede estar vacío")
    @Positive(message = "El monto del terminal POS debe ser positivo")
    @Min(value = 1, message = "El monto del terminal POS debe ser mayor a 0")
    private int amountClosingPos;

    @NotBlank(message = "El comentario es obligatorio")
    private String comment;
}
