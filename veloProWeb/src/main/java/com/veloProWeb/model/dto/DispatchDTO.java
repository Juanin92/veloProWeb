package com.veloProWeb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DispatchDTO {
    private Long id;
    private String trackingNumber;
    private String status;
    private String address;
    private String comment;
    private String customer;
    private boolean hasSale;
    private LocalDate created;
    private LocalDate deliveryDate;
    private List<DetailSaleDTO> detailSaleDTOS = new ArrayList<>();
}
