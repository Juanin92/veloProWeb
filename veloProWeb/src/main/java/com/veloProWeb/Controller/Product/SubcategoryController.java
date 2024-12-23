package com.veloProWeb.Controller.Product;

import com.veloProWeb.Model.Entity.Product.BrandProduct;
import com.veloProWeb.Model.Entity.Product.CategoryProduct;
import com.veloProWeb.Model.Entity.Product.SubcategoryProduct;
import com.veloProWeb.Service.Product.Interfaces.ISubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subcategoria")
@CrossOrigin(origins = "http://localhost:4200")
public class SubcategoryController {

    @Autowired
    private ISubcategoryService subcategoryService;

    @GetMapping
    public ResponseEntity<List<SubcategoryProduct>> getAllSubcategories(){
        return ResponseEntity.ok(subcategoryService.getAll());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createSubcategories(@RequestBody SubcategoryProduct subcategory, @RequestParam CategoryProduct category){
        Map<String, String> response = new HashMap<>();
        try {
            subcategoryService.save(subcategory, category);
            response.put("message", "Subcategoría registrada correctamente");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
