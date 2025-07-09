package com.veloproweb.model.dto.purchase;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestDTO {

    @NotBlank(message = "El rut del proveedor es obligatorio")
    @Pattern(regexp = "^\\d{7,8}-[\\dKk]$", message = "El rut no tiene un formato válido")
    private String supplier;

    @NotBlank(message = "Tipo de documento es obligatorio")
    private String documentType;

    @NotBlank(message = "Documento es obligatorio")
    private String document;

    @NotNull(message = "La fecha no puede ser nula.")
    private LocalDate date;

    @NotNull(message = "El valor del impuesto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private int tax;

    @NotNull(message = "total de la compra es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private int total;

    @NotNull(message = "Debe contener listado del detalle de la compra")
    private List<PurchaseDetailRequestDTO> detailList;
}
