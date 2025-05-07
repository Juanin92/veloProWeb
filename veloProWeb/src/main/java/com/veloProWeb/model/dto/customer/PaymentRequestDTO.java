package com.veloProWeb.model.dto.customer;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {

    @NotEmpty(message = "El ID del ticket no puede estar vacío")
    @NotNull(message = "La lista de IDs de tickets no puede ser nula")
    private List<Long> ticketIDs;

    @NotBlank(message = "El ID del cliente no puede estar vacío")
    private Long customerID;

    @NotBlank(message = "El monto no puede estar vacío")
    @Positive(message = "El monto debe ser positivo")
    @Min(value = 1, message = "El monto debe ser mayor a 0")
    private int amount;

    @NotBlank(message = "El comentario no puede estar vacío")
    private String comment;

    @NotBlank(message = "El monto total ya pagado no puede estar vacío")
    @Positive(message = "El monto debe ser positivo")
    private int totalPaymentPaid;
}
