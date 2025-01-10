package com.veloProWeb.Service.Purchase.Interfaces;

import com.veloProWeb.Model.DTO.DetailPurchaseDTO;
import com.veloProWeb.Model.DTO.PurchaseRequestDTO;
import com.veloProWeb.Model.Entity.Purchase.Purchase;

import java.util.List;

public interface IPurchaseService {
    Purchase createPurchase(PurchaseRequestDTO dto);
    Long totalPurchase();
    int totalPricePurchase(List<DetailPurchaseDTO> dto);
}
