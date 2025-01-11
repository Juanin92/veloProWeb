package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.DTO.DetailPurchaseDTO;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.PurchaseDetail;
import com.veloProWeb.Repository.Purchase.PurchaseDetailRepo;
import com.veloProWeb.Service.Product.Interfaces.IProductService;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseDetailService implements IPurchaseDetailService {

    @Autowired private PurchaseDetailRepo purchaseDetailRepo;
    @Autowired private IProductService productService;

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
        }
    }

    /**
     * Obtiene el registro de todos los detalles de compras
     * @return - Lista con los detalles de compras
     */
    @Override
    public List<PurchaseDetail> getAll() {
        return purchaseDetailRepo.findAll();
    }
}
