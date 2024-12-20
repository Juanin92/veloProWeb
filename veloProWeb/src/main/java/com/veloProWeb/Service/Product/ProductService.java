package com.veloProWeb.Service.Product;

import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Enum.StatusProduct;
import com.veloProWeb.Repository.Product.ProductRepo;
import com.veloProWeb.Service.Product.Interfaces.IProductService;
import com.veloProWeb.Validation.ProductValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired private ProductRepo productRepo;
    @Autowired private ProductValidator validator;

    @Override
    public void create(Product product) {
        validator.validateNewProduct(product);
        product.setStatus(false);
        product.setStatusProduct(StatusProduct.UNAVAILABLE);
        product.setBuyPrice(0);
        product.setSalePrice(0);
        product.setStock(0);
        productRepo.save(product);
    }

    @Override
    public void update(Product product){
        if (product.getStock() > 0){
            product.setStatus(true);
            product.setStatusProduct(StatusProduct.AVAILABLE);
            productRepo.save(product);
        }else {
            product.setStatus(false);
            product.setStatusProduct(StatusProduct.UNAVAILABLE);
            productRepo.save(product);
        }
    }

    @Override
    public void active(Product product) {
        product.setStatusProduct(StatusProduct.UNAVAILABLE);
        productRepo.save(product);
    }

    @Override
    public void updateStockPurchase(Product product, int price, int quantity) {
        product.setBuyPrice(price);
        product.setStock(product.getStock() + quantity);
        update(product);
    }

    @Override
    public void updateStockSale(Product product, int quantity) {
        product.setStock(product.getStock() - quantity);
        update(product);
    }

    @Override
    public void delete(Product product) {
        product.setStatus(false);
        product.setStatusProduct(StatusProduct.DISCONTINUED);
        productRepo.save(product);
    }

    @Override
    public List<Product> getAll() {
        return productRepo.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElse(null);
    }
}
