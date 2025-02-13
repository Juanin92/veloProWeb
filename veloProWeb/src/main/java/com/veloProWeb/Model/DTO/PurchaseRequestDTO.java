package com.veloProWeb.Model.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseRequestDTO {
    private Long id;
    private LocalDate date;
    private Long idSupplier;
    private String documentType;
    private String document;
    private int tax;
    private int total;
    private List<DetailPurchaseDTO> detailList;
}
