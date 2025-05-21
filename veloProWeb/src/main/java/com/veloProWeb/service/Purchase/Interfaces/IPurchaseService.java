package com.veloProWeb.service.Purchase.Interfaces;

import com.veloProWeb.model.dto.purchase.PurchaseRequestDTO;
import com.veloProWeb.model.dto.purchase.PurchaseResponseDTO;
import com.veloProWeb.model.entity.Purchase.Purchase;

import java.util.List;

public interface IPurchaseService {
    Purchase createPurchase(PurchaseRequestDTO dto);
    Long totalPurchase();
    List<PurchaseResponseDTO> getAllPurchases();
}
