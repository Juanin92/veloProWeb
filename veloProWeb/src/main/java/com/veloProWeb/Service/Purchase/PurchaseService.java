package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Repository.Purchase.PurchaseRepo;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PurchaseService implements IPurchaseService {

    @Autowired private PurchaseRepo purchaseRepo;

    @Override
    public Purchase save(LocalDate date, Supplier supplier, String documentType, String document, int tax, int total) {
        return null;
    }

    @Override
    public Long totalPurchase() {
        return 0L;
    }
}
