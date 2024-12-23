package com.veloProWeb.Controller.Product;

import com.veloProWeb.Model.Entity.Product.BrandProduct;
import com.veloProWeb.Service.Product.BrandService;
import com.veloProWeb.Service.Product.Interfaces.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/marcas")
@CrossOrigin(origins = "http://localhost:4200")
public class BrandController {

    @Autowired private IBrandService brandService;

    @GetMapping
    public ResponseEntity<List<BrandProduct>> getAllBrands(){
        return ResponseEntity.ok(brandService.getAll());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createBrand(@RequestBody BrandProduct brand){
        Map<String, String> response = new HashMap<>();
        try {
            brandService.save(brand);
            response.put("message", "Marca registrada correctamente");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
