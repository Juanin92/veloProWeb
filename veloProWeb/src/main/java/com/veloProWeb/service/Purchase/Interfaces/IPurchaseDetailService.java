package com.veloProWeb.service.Purchase.Interfaces;

import com.veloProWeb.model.dto.purchase.DetailPurchaseDTO;
import com.veloProWeb.model.dto.purchase.DetailPurchaseRequestDTO;
import com.veloProWeb.model.entity.Purchase.Purchase;

import java.util.List;

public interface IPurchaseDetailService {
    void createDetailPurchase(List<DetailPurchaseDTO> dtoList, Purchase purchase);
    List<DetailPurchaseRequestDTO> getPurchaseDetails(Long idPurchase);
}
