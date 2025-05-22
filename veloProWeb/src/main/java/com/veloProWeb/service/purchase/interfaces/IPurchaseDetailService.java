package com.veloProWeb.service.purchase.interfaces;

import com.veloProWeb.model.dto.purchase.PurchaseDetailRequestDTO;
import com.veloProWeb.model.entity.purchase.Purchase;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IPurchaseDetailService {
    void createPurchaseDetail(UserDetails user, List<PurchaseDetailRequestDTO> detailDtos, Purchase purchase);
}
