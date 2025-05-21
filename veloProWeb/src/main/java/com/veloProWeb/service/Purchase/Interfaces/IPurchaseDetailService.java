package com.veloProWeb.service.Purchase.Interfaces;

import com.veloProWeb.model.dto.purchase.PurchaseDetailRequestDTO;
import com.veloProWeb.model.entity.Purchase.Purchase;

import java.util.List;

public interface IPurchaseDetailService {
    void createPurchaseDetail(List<PurchaseDetailRequestDTO> detailDtos, Purchase purchase);
}
