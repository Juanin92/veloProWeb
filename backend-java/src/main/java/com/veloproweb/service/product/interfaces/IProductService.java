package com.veloproweb.service.product.interfaces;

import com.veloproweb.model.dto.product.ProductRequestDTO;
import com.veloproweb.model.dto.product.ProductResponseDTO;
import com.veloproweb.model.dto.product.ProductUpdatedRequestDTO;
import com.veloproweb.model.entity.product.Product;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IProductService {

    void create(ProductRequestDTO dto, UserDetails userDetails);
    void updateProductInfo(ProductUpdatedRequestDTO dto, UserDetails userDetails);
    List<ProductResponseDTO> getAll();
    Product getProductById(Long id);
    void discontinueProduct(ProductUpdatedRequestDTO dto, UserDetails userDetails);
    void updateStockStatus(Product product);
    void reactive(ProductUpdatedRequestDTO dto, UserDetails userDetails);
    void updateStockPurchase(Product product, int price, int quantity);
    void updateStockSale(Product product, int quantity);
    void updateStockAndReserveDispatch(Product product, int quantity, boolean success);
}
