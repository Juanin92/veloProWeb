package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.DTO.DetailPurchaseDTO;
import com.veloProWeb.Model.DTO.DetailSaleDTO;
import com.veloProWeb.Model.DTO.PurchaseRequestDTO;
import com.veloProWeb.Model.DTO.SaleRequestDTO;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.PurchaseDetail;
import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Entity.Sale.SaleDetail;
import com.veloProWeb.Repository.Purchase.PurchaseRepo;
import com.veloProWeb.Repository.Purchase.SupplierRepo;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseService;
import com.veloProWeb.Validation.PurchaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService implements IPurchaseService {

    @Autowired private PurchaseRepo purchaseRepo;
    @Autowired private SupplierRepo supplierRepo;
    @Autowired private PurchaseValidator validator;

    /**
     * Crea una nueva compra.
     * @param dto - contiene los datos necesarios para crear la compra.
     * @return - objeto Compra que representa la compra creada y persistida en la BD.
     * @throws IllegalArgumentException si el proveedor especificado no existe en la BD
     */
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

    @Override
    public List<PurchaseRequestDTO> getAllPurchases() {
        List<Purchase> purchases = purchaseRepo.findAll();
        List<PurchaseRequestDTO> dtoList = new ArrayList<>();
        for (Purchase purchase : purchases){
            PurchaseRequestDTO dto = convertToPurchaseRequestDTO(purchase);
            dtoList.add(dto);
        }
        return dtoList;
    }

    /**
     *  Convierte un objeto Purchase a un DTO
     * @param purchase - la compra que se va convertir
     * @return - objeto DTO que contiene los detalle de la compra
     */
    private PurchaseRequestDTO convertToPurchaseRequestDTO(Purchase purchase){
        PurchaseRequestDTO dto = new PurchaseRequestDTO();
        dto.setId(purchase.getId());
        dto.setDate(purchase.getDate());
        dto.setIdSupplier(purchase.getSupplier().getId());
        dto.setDocumentType(purchase.getDocumentType());
        dto.setDocument(purchase.getDocument());
        dto.setTax(purchase.getIva());
        dto.setTotal(purchase.getPurchaseTotal());
        dto.setDetailList(null);
        return dto;
    }
}
