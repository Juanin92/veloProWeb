package com.veloProWeb.Service.Purchase.Interfaces;

import com.veloProWeb.Model.DTO.DetailPurchaseDTO;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.Supplier;

import java.time.LocalDate;
import java.util.List;

public interface IPurchaseService {
    Purchase createPurchase(LocalDate date, Supplier supplier, String documentType, String document, int tax, int total);
    Long totalPurchase();
    int totalPricePurchase(List<DetailPurchaseDTO> dto);
}
