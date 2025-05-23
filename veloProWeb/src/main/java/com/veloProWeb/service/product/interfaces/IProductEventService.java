package com.veloProWeb.service.product.interfaces;

import com.veloProWeb.model.dto.product.ProductUpdatedRequestDTO;
import com.veloProWeb.model.entity.product.Product;
import org.springframework.security.core.userdetails.UserDetails;

public interface IProductEventService {
    void handleCreateRegister(Product product, String comment, UserDetails userDetails);
    void isChangeStockOriginalValue(Product product, int originalStock, ProductUpdatedRequestDTO dto,
                                    UserDetails userDetails);
    void handleNoStockAlert(Product product);
    void handleCriticalStockAlert(Product product);
    void checkLowSales(Product product);
}
