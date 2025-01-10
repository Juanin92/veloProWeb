package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.DTO.DetailPurchaseDTO;
import com.veloProWeb.Model.DTO.PurchaseRequestDTO;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Repository.Purchase.PurchaseRepo;
import com.veloProWeb.Repository.Purchase.SupplierRepo;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseService;
import com.veloProWeb.Validation.PurchaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService implements IPurchaseService {

    @Autowired private PurchaseRepo purchaseRepo;
    @Autowired private SupplierRepo supplierRepo;
    @Autowired private PurchaseValidator validator;

    /**
     * Crea una nueva compra.
     * Inicializa y registra la compra en la BD
     * @param dto -
     * @return - Compra creada con todos sus valores registrados
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
     * Calcula la suma total de los montos en los detalles de compra proporcionados en la lista.
     * Recorre la lista y mapea cada detalle de compra obteniendo el total y sumándolos.
     * @param dto - Lista que contiene los detalles de la compra
     * @return - La suma de los totales de cada detalle en la lista.
     */
    @Override
    public int totalPricePurchase(List<DetailPurchaseDTO> dto) {
        return dto.stream()
                .mapToInt(DetailPurchaseDTO::getTotal)
                .sum();
    }
}
