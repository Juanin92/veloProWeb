package com.veloProWeb.Model.DTO;

import com.veloProWeb.Model.Entity.Sale.SaleDetail;

import java.time.LocalDate;
import java.util.List;

public class SaleRequestDTO {
    private Long id;
    private LocalDate date;
    private Long idCustomer;
    private String documentType;
    private String document;
    private int tax;
    private int total;
    private List<SaleDetail> detailList;
}
