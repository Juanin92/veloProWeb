package com.veloProWeb.model.dto.customer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TicketRequestDTO {

    @NotNull(message = "ID del cliente no puede estar vacío")
    private Long customerID;

    @NotNull(message = "Número de ticket no puede estar vacío")
    @Positive(message = "Número de ticket debe ser positivo")
    private Long number;

    @NotNull(message = "Total del ticket no puede estar vacío")
    private int total;
}
