package com.veloproweb.service.purchase.interfaces;

import com.veloproweb.model.dto.purchase.PurchaseRequestDTO;
import com.veloproweb.model.dto.purchase.PurchaseResponseDTO;
import com.veloproweb.model.entity.purchase.Purchase;

import java.util.List;

public interface IPurchaseService {
    Purchase createPurchase(PurchaseRequestDTO dto);
    Long totalPurchase();
    List<PurchaseResponseDTO> getAllPurchases();
}
