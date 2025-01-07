package com.veloProWeb.Model.DTO;

import com.veloProWeb.Model.Entity.Purchase.Supplier;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PurchaseRequestDTO {
    private LocalDate date;
    private Supplier supplier;
    private String documentType;
    private String document;
    private int tax;
    private int total;
}
