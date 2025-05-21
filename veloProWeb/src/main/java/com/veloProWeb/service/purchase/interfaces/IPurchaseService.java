package com.veloProWeb.service.purchase.interfaces;

import com.veloProWeb.model.dto.purchase.PurchaseRequestDTO;
import com.veloProWeb.model.dto.purchase.PurchaseResponseDTO;
import com.veloProWeb.model.entity.purchase.Purchase;

import java.util.List;

public interface IPurchaseService {
    Purchase createPurchase(PurchaseRequestDTO dto);
    Long totalPurchase();
    List<PurchaseResponseDTO> getAllPurchases();
}
