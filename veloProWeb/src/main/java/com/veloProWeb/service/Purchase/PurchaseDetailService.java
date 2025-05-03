package com.veloProWeb.service.Purchase;

import com.veloProWeb.model.dto.DetailPurchaseDTO;
import com.veloProWeb.model.dto.DetailPurchaseRequestDTO;
import com.veloProWeb.model.entity.Product.Product;
import com.veloProWeb.model.entity.Purchase.Purchase;
import com.veloProWeb.model.entity.Purchase.PurchaseDetail;
import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.repository.Purchase.PurchaseDetailRepo;
import com.veloProWeb.service.Product.Interfaces.IProductService;
import com.veloProWeb.service.Purchase.Interfaces.IPurchaseDetailService;
import com.veloProWeb.service.Purchase.Interfaces.IPurchaseService;
import com.veloProWeb.service.Report.IkardexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseDetailService implements IPurchaseDetailService {

    @Autowired private PurchaseDetailRepo purchaseDetailRepo;
    @Autowired private IProductService productService;
    @Autowired private IPurchaseService purchaseService;
    @Autowired private IkardexService kardexService;

    /**
     * Crear detalle de compras proporcionadas
     * Busca el producto correspondiente en el sistema utilizando por ID.
     * Actualiza el stock del producto mediante el servicio de Producto
     * @param dtoList - Lista de objetos dto que contienen los detalles de la compra.
     * @param purchase - Objeto que representa la compra asociada a los detalles.
     * @throws IllegalArgumentException Si no se encuentra un producto con el ID proporcionado en alguno de los detalles.
     */
    @Override
    public void createDetailPurchase(List<DetailPurchaseDTO> dtoList, Purchase purchase) {
        for (DetailPurchaseDTO dto : dtoList){
            PurchaseDetail purchaseDetail =  new PurchaseDetail();
            purchaseDetail.setId(null);
            purchaseDetail.setPrice(dto.getPrice());
            purchaseDetail.setQuantity(dto.getQuantity());
            purchaseDetail.setTax(dto.getTax());
            purchaseDetail.setTotal(dto.getTotal());
            Product product = productService.getProductById(dto.getIdProduct());
            purchaseDetail.setProduct(product);
            purchaseDetail.setPurchase(purchase);
            purchaseDetailRepo.save(purchaseDetail);
            productService.updateStockPurchase(product, purchaseDetail.getPrice(), purchaseDetail.getQuantity());
            kardexService.addKardex(product, dto.getQuantity(), "Compra / " +
                    purchase.getDocumentType() + " - " + purchase.getDocument(), MovementsType.ENTRADA);
        }
    }

    /**
     * Obtiene una lista de detalles de una compra en específico
     * @param idPurchase - Identificador de la compra
     * @return - Lista de dto con los detalles de la compra encontrados
     */
    @Override
    public List<DetailPurchaseRequestDTO> getPurchaseDetails(Long idPurchase) {
        List<DetailPurchaseRequestDTO> purchaseRequestDTOS = new ArrayList<>();
        List<PurchaseDetail> purchaseDetails = purchaseDetailRepo.findByPurchaseId(idPurchase);
        Optional<Purchase> purchase = purchaseService.getPurchaseById(idPurchase);
        if (purchase.isPresent()) {
            for (PurchaseDetail purchaseDetail : purchaseDetails){
                DetailPurchaseRequestDTO dto = new DetailPurchaseRequestDTO();
                dto.setPrice(purchaseDetail.getPrice());
                dto.setQuantity(purchaseDetail.getQuantity());
                dto.setTax(purchaseDetail.getTax());
                dto.setTotal(purchaseDetail.getTotal());
                dto.setDescriptionProduct(purchaseDetail.getProduct().getDescription());
                purchaseRequestDTOS.add(dto);
            }
        }
        return purchaseRequestDTOS;
    }
}
