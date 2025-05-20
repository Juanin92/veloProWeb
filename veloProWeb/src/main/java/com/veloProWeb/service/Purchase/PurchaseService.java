package com.veloProWeb.service.Purchase;

import com.veloProWeb.model.dto.PurchaseRequestDTO;
import com.veloProWeb.model.entity.Purchase.Purchase;
import com.veloProWeb.model.entity.Purchase.Supplier;
import com.veloProWeb.repository.Purchase.PurchaseRepo;
import com.veloProWeb.repository.Purchase.SupplierRepo;
import com.veloProWeb.service.Purchase.Interfaces.IPurchaseService;
import com.veloProWeb.validation.PurchaseValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PurchaseService implements IPurchaseService {

    private final PurchaseRepo purchaseRepo;
    private final SupplierRepo supplierRepo;
    private final PurchaseValidator validator;

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

    /**
     * Obtiene todas las compras registradas
     * @return - Lista dto con las compras registradas
     */
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
     * Obtiene una compra especifíca
     * @param id - Identificador de la compra a buscar
     * @return - Una compra si está presente o un objeto vació
     */
    @Override
    public Optional<Purchase> getPurchaseById(Long id) {
        return purchaseRepo.findById(id);
    }

    /**
     *  Convierte un objeto Purchase a un dto
     * @param purchase - la compra que se va convertir
     * @return - objeto dto que contiene los detalle de la compra
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
