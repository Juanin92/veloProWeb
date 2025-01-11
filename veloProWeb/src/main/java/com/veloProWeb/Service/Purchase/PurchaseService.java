package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.DTO.PurchaseRequestDTO;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Repository.Purchase.PurchaseRepo;
import com.veloProWeb.Repository.Purchase.SupplierRepo;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseService;
import com.veloProWeb.Validation.PurchaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PurchaseService implements IPurchaseService {

    @Autowired private PurchaseRepo purchaseRepo;
    @Autowired private SupplierRepo supplierRepo;
    @Autowired private PurchaseValidator validator;


    @Override
    public Purchase createPurchase(PurchaseRequestDTO dto) {
        Purchase purchase =  new Purchase();
        purchase.setId(null);
        purchase.setDocument(dto.getDocument());
        purchase.setDocumentType(dto.getDocumentType());
        purchase.setIva(dto.getTax());
        purchase.setPurchaseTotal(dto.getTotal());
        purchase.setDate(dto.getDate());
        Optional<Supplier> supplier = supplierRepo.findById(dto.getIdSupplier());
        purchase.setSupplier(supplier.orElseThrow(() -> new IllegalArgumentException("No ha seleccionado un proveedor")));
        validator.validate(purchase);
        purchaseRepo.save(purchase);
        return purchase;
    }

    /**
     * Obtiene el número total de compras registradas
     * @return - cantidad total de compras
     */
    @Override
    public Long totalPurchase() {
        return purchaseRepo.count();
    }
}
