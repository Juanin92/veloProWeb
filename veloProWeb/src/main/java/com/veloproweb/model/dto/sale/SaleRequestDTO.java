package com.veloproweb.model.dto.sale;

import com.veloproweb.model.Enum.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleRequestDTO {

    private Long idCustomer;
    private Long idDispatch;

    @NotNull(message = "Un método de pago es obligatorio")
    private PaymentMethod paymentMethod;

    @NotNull(message = "El valor del impuesto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private int tax;

    @NotNull(message = "total de la venta es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private int total;

    @NotNull(message = "El descuento de la venta es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private int discount;

    @NotBlank(message = "El comentario de la venta es obligatorio")
    private String comment;

    @NotNull(message = "Debe contener listado del detalle la venta")
    private List<SaleDetailRequestDTO> detailList;
}
