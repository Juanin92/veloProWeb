package com.veloProWeb.Controller.Product;

import com.veloProWeb.Model.Entity.Product.BrandProduct;
import com.veloProWeb.Model.Entity.Product.CategoryProduct;
import com.veloProWeb.Service.Product.Interfaces.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categoria")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryProduct>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createCategory(@RequestBody CategoryProduct category){
        Map<String, String> response = new HashMap<>();
        try {
            categoryService.save(category);
            response.put("message", "Categoría registrada correctamente");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
