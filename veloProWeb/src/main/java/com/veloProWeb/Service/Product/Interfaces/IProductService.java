package com.veloProWeb.Service.Product.Interfaces;

import com.veloProWeb.Model.Entity.Product.Product;

import java.util.List;

public interface IProductService {

    void create(Product product);
    List<Product> getAll();
    Product getProductById(Long id);
    void delete(Product product);
    void update(Product product);
    void active(Product product);
    void updateStockPurchase(Product product, int price, int quantity);
    void updateStockSale(Product product, int quantity);
    void checkAndCreateAlertsByProduct();
}
