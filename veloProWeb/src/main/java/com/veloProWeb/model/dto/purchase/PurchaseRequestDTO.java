package com.veloProWeb.model.dto.purchase;

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
    private Long id;
    private LocalDate date;
    private Long idSupplier;
    private String documentType;
    private String document;
    private int tax;
    private int total;
    private List<DetailPurchaseDTO> detailList;
}
