package com.veloProWeb.Controller.Product;

import com.veloProWeb.Model.Entity.Product.SubcategoryProduct;
import com.veloProWeb.Service.Product.Interfaces.ISubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar operaciones relacionadas con las subcategorías de productos.
 * Este controlador proporciona endpoints para obtener todas las subcategorías y crear nuevas subcategorías.
 */
@RestController
@RequestMapping("/subcategoria")
@CrossOrigin(origins = "http://localhost:4200")
public class SubcategoryController {

    @Autowired
    private ISubcategoryService subcategoryService;

    /**
     * Obtiene una lista de todas las subcategorías
     * @return - ResponseEntity con una lista de las subcategorías
     */
    @GetMapping
    public ResponseEntity<List<SubcategoryProduct>> getAllSubcategories(){
        return ResponseEntity.ok(subcategoryService.getAll());
    }

    /**
     * Obtiene la lista de subcategorías asociadas a una categoría específica por su ID.
     * @param id - Identificador de la categoría cuyas subcategorías se desean obtener.
     * @return - ResponseEntity con una lista de las subcategorías
     */
    @GetMapping("/{id}")
    public ResponseEntity<List<SubcategoryProduct>> getAllSubcategoriesByCategory(@PathVariable Long id){
        return ResponseEntity.ok(subcategoryService.getSubcategoryByCategoryID(id));
    }

    /**
     * Crea una nueva subcategoría
     * @param subcategory - Subcategoría con los datos a crear
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createSubcategories(@RequestBody SubcategoryProduct subcategory){
        Map<String, String> response = new HashMap<>();
        try {
            subcategoryService.save(subcategory);
            response.put("message", "Subcategoría registrada correctamente");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
