package com.veloProWeb.Service.Purchase.Interfaces;

import com.veloProWeb.Model.DTO.PurchaseRequestDTO;
import com.veloProWeb.Model.Entity.Purchase.Purchase;

import java.util.List;
import java.util.Optional;

public interface IPurchaseService {
    Purchase createPurchase(PurchaseRequestDTO dto);
    Long totalPurchase();
    List<PurchaseRequestDTO> getAllPurchases();
    Optional<Purchase> getPurchaseById(Long id);
}
