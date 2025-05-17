package com.veloProWeb.service.product.interfaces;

import com.veloProWeb.model.dto.product.ProductRequestDTO;
import com.veloProWeb.model.dto.product.ProductResponseDTO;
import com.veloProWeb.model.dto.product.ProductUpdatedRequestDTO;
import com.veloProWeb.model.entity.product.Product;

import java.util.List;

public interface IProductService {

    void create(ProductRequestDTO dto);
    void updateProductInfo(ProductUpdatedRequestDTO dto);
    List<ProductResponseDTO> getAll();
    Product getProductById(Long id);
    void discontinueProduct(ProductUpdatedRequestDTO dto);
    void updateStockStatus(Product product);
    void reactive(ProductUpdatedRequestDTO dto);
    void updateStockPurchase(Product product, int price, int quantity);
    void updateStockSale(Product product, int quantity);
    void checkAndCreateAlertsByProduct();
    void updateStockAndReserveDispatch(Product product, int quantity, boolean success);
}
