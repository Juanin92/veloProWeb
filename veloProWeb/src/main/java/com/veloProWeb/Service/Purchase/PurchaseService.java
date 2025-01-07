package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Repository.Purchase.PurchaseRepo;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseService;
import com.veloProWeb.Validation.PurchaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PurchaseService implements IPurchaseService {

    @Autowired private PurchaseRepo purchaseRepo;
    @Autowired private PurchaseValidator validator;

    @Override
    public Purchase save(LocalDate date, Supplier supplier, String documentType, String document, int tax, int total) {
        Purchase purchase =  new Purchase();
        purchase.setDate(date);
        purchase.setSupplier(supplier);
        purchase.setDocumentType(documentType);
        purchase.setDocument(document);
        purchase.setIva(tax);
        purchase.setPurchaseTotal(total);
        validator.validate(purchase);
        purchaseRepo.save(purchase);
        return purchase;
    }

    @Override
    public Long totalPurchase() {
        return purchaseRepo.count();
    }
}
