package com.veloProWeb.Service.Purchase.Interfaces;

import com.veloProWeb.Model.DTO.DetailPurchaseDTO;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.PurchaseDetail;

import java.util.List;

public interface IPurchaseDetailService {
    void createDetailPurchase(List<DetailPurchaseDTO> dtoList, Purchase purchase);
    List<PurchaseDetail> getAll();
}
