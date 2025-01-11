package com.veloProWeb.Service.Purchase.Interfaces;

import com.veloProWeb.Model.DTO.PurchaseRequestDTO;
import com.veloProWeb.Model.Entity.Purchase.Purchase;

public interface IPurchaseService {
    Purchase createPurchase(PurchaseRequestDTO dto);
    Long totalPurchase();
}
