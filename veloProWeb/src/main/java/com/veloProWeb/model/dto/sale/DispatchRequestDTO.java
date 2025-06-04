package com.veloProWeb.model.dto.sale;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DispatchRequestDTO {

    @NotBlank(message = "La dirección del despacho es obligatoria")
    private String address;

    @NotBlank(message = "El cliente para el despacho es obligatorio")
    private String customer;

    private String comment;

    @NotNull(message = "Debe contener al menos un producto en el despacho")
    private List<SaleDetailRequestDTO> detailSaleDTOList = new ArrayList<>();
}
