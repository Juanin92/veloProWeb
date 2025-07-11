package com.veloproweb.model.dto.purchase;

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
public class PurchaseResponseDTO {

    private String document;
    private String documentType;
    private LocalDate date;
    private int iva;
    private int purchaseTotal;
    private String supplier;
    private List<PurchaseDetailResponseDTO> detailsList;
}
