package com.veloProWeb.Controller.Product;

import com.veloProWeb.Model.DTO.ProductDTO;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Repository.Product.ProductRepo;
import com.veloProWeb.Service.Product.Interfaces.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stock")
@CrossOrigin(origins = "http://localhost:4200")
public class StockController {

    @Autowired private IProductService productService;

    @GetMapping
    public List<ProductDTO> getListProducts(){
        return productService.getAll().stream()
                .map(product -> new ProductDTO(
                        product.getId(),
                        product.getDescription(),
                        product.getSalePrice(),
                        product.getBuyPrice(),
                        product.getStock(),
                        product.isStatus(),
                        product.getStatusProduct(),
                        product.getBrand().getName(),
                        product.getUnit().getNameUnit(),
                        product.getSubcategoryProduct().getName(),
                        product.getCategory().getName()
                )).collect(Collectors.toList());
    }
}
