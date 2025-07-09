package com.veloproweb.service.purchase.interfaces;

import com.veloproweb.model.dto.purchase.PurchaseDetailRequestDTO;
import com.veloproweb.model.entity.purchase.Purchase;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IPurchaseDetailService {
    void createPurchaseDetail(UserDetails user, List<PurchaseDetailRequestDTO> detailDtos, Purchase purchase);
}
