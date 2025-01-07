package com.veloProWeb.Service.Purchase.Interfaces;

import com.veloProWeb.Model.DTO.DetailPurchaseDTO;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.PurchaseDetail;

import java.util.List;

public interface IPurchaseDetailService {
    void createDetailPurchase(DetailPurchaseDTO dto, Purchase purchase, Product product);
    List<PurchaseDetail> getAll();
    DetailPurchaseDTO createDTO(Product product);
    boolean deleteProduct(Long id, List<DetailPurchaseDTO> dto);
}
