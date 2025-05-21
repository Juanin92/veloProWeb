package com.veloProWeb.service.purchase.interfaces;

import com.veloProWeb.model.dto.purchase.PurchaseDetailRequestDTO;
import com.veloProWeb.model.entity.purchase.Purchase;

import java.util.List;

public interface IPurchaseDetailService {
    void createPurchaseDetail(List<PurchaseDetailRequestDTO> detailDtos, Purchase purchase);
}
