package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.Entity.Purchase.PurchaseDetail;
import com.veloProWeb.Repository.Purchase.PurchaseDetailRepo;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseDetailService implements IPurchaseDetailService {

    @Autowired private PurchaseDetailRepo purchaseDetailRepo;

    @Override
    public List<PurchaseDetail> getAll() {
        return List.of();
    }
}
