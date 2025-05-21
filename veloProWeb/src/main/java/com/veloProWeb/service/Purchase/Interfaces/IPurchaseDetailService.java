package com.veloProWeb.service.Purchase.Interfaces;

import com.veloProWeb.model.dto.purchase.DetailPurchaseRequestDTO;
import com.veloProWeb.model.entity.Purchase.Purchase;

import java.util.List;

public interface IPurchaseDetailService {
    void createDetailPurchase(List<DetailPurchaseRequestDTO> dtoList, Purchase purchase);
}
