package com.veloProWeb.Controller.Product;

import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Service.Product.Interfaces.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stock")
@CrossOrigin(origins = "http://localhost:4200")
public class StockController {

    @Autowired private IProductService productService;

    @GetMapping()
    public ResponseEntity<List<Product>> getListProducts(){
        return ResponseEntity.ok(productService.getAll());
    }
}
