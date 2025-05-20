package com.veloProWeb.service.Purchase.Interfaces;

import com.veloProWeb.model.dto.purchase.PurchaseRequestDTO;
import com.veloProWeb.model.entity.Purchase.Purchase;

import java.util.List;
import java.util.Optional;

public interface IPurchaseService {
    Purchase createPurchase(PurchaseRequestDTO dto);
    Long totalPurchase();
    List<PurchaseRequestDTO> getAllPurchases();
    Optional<Purchase> getPurchaseById(Long id);
}
