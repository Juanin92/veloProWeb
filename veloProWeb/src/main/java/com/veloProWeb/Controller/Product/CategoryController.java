package com.veloProWeb.Controller.Product;

import com.veloProWeb.Model.Entity.Product.CategoryProduct;
import com.veloProWeb.Service.Product.Interfaces.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar operaciones relacionadas con las categorías de productos.
 * Este controlador proporciona endpoints para obtener todas las categorías y crear nuevas categorías.
 */
@RestController
@RequestMapping("/categoria")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    /**
     * Obtiene una lista de todas las categorías
     * @return - ResponseEntity con una lista de las categorías
     */
    @GetMapping
    public ResponseEntity<List<CategoryProduct>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAll());
    }

    /**
     * Crea una nueva categoría
     * @param category - Categoría con los datos a crear
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
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
