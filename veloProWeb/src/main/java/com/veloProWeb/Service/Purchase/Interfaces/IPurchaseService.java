package com.veloProWeb.Service.Purchase.Interfaces;

import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.Supplier;

import java.time.LocalDate;

public interface IPurchaseService {
    Purchase createPurchase(LocalDate date, Supplier supplier, String documentType, String document, int tax, int total);
    Long totalPurchase();
//    int totalPricePurchase(List<DetailPurchaseDTO> dto);
}
