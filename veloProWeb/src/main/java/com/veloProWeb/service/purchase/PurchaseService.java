package com.veloProWeb.service.purchase;

import com.veloProWeb.mapper.PurchaseMapper;
import com.veloProWeb.model.dto.purchase.PurchaseRequestDTO;
import com.veloProWeb.model.dto.purchase.PurchaseResponseDTO;
import com.veloProWeb.model.entity.purchase.Purchase;
import com.veloProWeb.model.entity.purchase.Supplier;
import com.veloProWeb.repository.purchase.PurchaseRepo;
import com.veloProWeb.service.purchase.interfaces.IPurchaseService;
import com.veloProWeb.service.purchase.interfaces.ISupplierService;
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

    /**
     * Crea una nueva compra.
     * @param dto - contiene los datos necesarios para crear la compra.
     * @return - objeto Compra que representa la compra creada y persistida en la BD.
     */
    @Transactional
    @Override
    public Purchase createPurchase(PurchaseRequestDTO dto) {
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
