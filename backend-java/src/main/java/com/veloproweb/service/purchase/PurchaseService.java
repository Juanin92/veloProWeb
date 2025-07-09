package com.veloproweb.service.purchase;

import com.veloproweb.mapper.PurchaseMapper;
import com.veloproweb.model.dto.purchase.PurchaseRequestDTO;
import com.veloproweb.model.dto.purchase.PurchaseResponseDTO;
import com.veloproweb.model.entity.purchase.Purchase;
import com.veloproweb.model.entity.purchase.Supplier;
import com.veloproweb.repository.purchase.PurchaseRepo;
import com.veloproweb.service.purchase.interfaces.IPurchaseService;
import com.veloproweb.service.purchase.interfaces.ISupplierService;
import com.veloproweb.validation.PurchaseValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PurchaseService implements IPurchaseService {

    private final PurchaseRepo purchaseRepo;
    private final ISupplierService supplierService;
    private final PurchaseMapper mapper;
    private final PurchaseValidator validator;

    /**
     * Crea una nueva compra.
     * @param dto - contiene los datos necesarios para crear la compra.
     * @return - objeto Compra que representa la compra creada y persistida en la BD.
     */
    @Transactional
    @Override
    public Purchase createPurchase(PurchaseRequestDTO dto) {
        validator.validateTotal(dto.getTotal(), dto.getDetailList());
        Supplier supplier = supplierService.getEntityByRut(dto.getSupplier());
        Purchase purchase =  mapper.toPurchaseEntity(dto, supplier);
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
     * @return - Lista DTO con las compras registradas
     */
    @Override
    public List<PurchaseResponseDTO> getAllPurchases() {
        return purchaseRepo.findAll().stream()
                .map(mapper::toPurchaseResponseDTO)
                .toList();
    }
}
