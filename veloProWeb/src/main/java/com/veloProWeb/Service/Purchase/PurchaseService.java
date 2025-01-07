package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.DTO.DetailPurchaseDTO;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Repository.Purchase.PurchaseRepo;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseService;
import com.veloProWeb.Validation.PurchaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PurchaseService implements IPurchaseService {

    @Autowired private PurchaseRepo purchaseRepo;
    @Autowired private PurchaseValidator validator;

    /**
     * Crea una nueva compra.
     * Inicializa y registra la compra en la BD
     * @param date - Fecha de la compra
     * @param supplier - Proveedor al que se realiza la compra.
     * @param documentType - Tipo de documento (Ej: boleta o factura)
     * @param document - Número de documento de la compra
     * @param tax - Impuesto aplicado a la compra
     * @param total - Total del monto de la compra
     * @return - Compra creada con todos sus valores registrados
     */
    @Override
    public Purchase createPurchase(LocalDate date, Supplier supplier, String documentType, String document, int tax, int total) {
        Purchase purchase =  new Purchase();
        purchase.setId(null);
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
