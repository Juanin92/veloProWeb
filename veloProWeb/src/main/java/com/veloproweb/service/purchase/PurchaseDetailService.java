package com.veloproweb.service.purchase;

import com.veloproweb.mapper.PurchaseMapper;
import com.veloproweb.model.dto.purchase.PurchaseDetailRequestDTO;
import com.veloproweb.model.entity.product.Product;
import com.veloproweb.model.entity.purchase.Purchase;
import com.veloproweb.model.entity.purchase.PurchaseDetail;
import com.veloproweb.model.Enum.MovementsType;
import com.veloproweb.repository.purchase.PurchaseDetailRepo;
import com.veloproweb.service.product.interfaces.IProductService;
import com.veloproweb.service.purchase.interfaces.IPurchaseDetailService;
import com.veloproweb.service.inventory.IKardexService;
import com.veloproweb.validation.PurchaseValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PurchaseDetailService implements IPurchaseDetailService {

    private final PurchaseDetailRepo purchaseDetailRepo;
    private final IProductService productService;
    private final IKardexService kardexService;
    private final PurchaseMapper mapper;
    private final PurchaseValidator validator;

    /**
     * Crear detalle de compras proporcionadas
     * Busca el producto correspondiente en el sistema utilizando por ID.
     * Actualiza el stock del producto mediante el servicio de Producto
     * @param detailDtos - Lista de objetos dto que contienen los detalles de la compra.
     * @param purchase - Objeto que representa la compra asociada a los detalles.
     */
    @Transactional
    @Override
    public void createPurchaseDetail(UserDetails user, List<PurchaseDetailRequestDTO> detailDtos,
                                     Purchase purchase) {
        validator.hasPurchase(purchase);
        List<PurchaseDetail> purchaseDetails = new ArrayList<>();
        for (PurchaseDetailRequestDTO dto : detailDtos){
            Product product = productService.getProductById(dto.getIdProduct());
            PurchaseDetail purchaseDetail = mapper.toPurchaseDetailEntity(dto, product, purchase);
            purchaseDetails.add(purchaseDetail);

            productService.updateStockPurchase(product, purchaseDetail.getPrice(), purchaseDetail.getQuantity());
            kardexService.addKardex(user, product, dto.getQuantity(), String.format("Compra / %s - %s",
                    purchase.getDocumentType(), purchase.getDocument()), MovementsType.ENTRADA);
        }
        purchaseDetailRepo.saveAll(purchaseDetails);
    }
}
