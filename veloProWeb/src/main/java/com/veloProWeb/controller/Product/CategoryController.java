package com.veloProWeb.controller.Product;

import com.veloProWeb.model.entity.Product.CategoryProduct;
import com.veloProWeb.service.Product.Interfaces.ICategoryService;
import com.veloProWeb.service.Record.IRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Autowired private ICategoryService categoryService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene una lista de todas las categorías
     * @return - ResponseEntity con una lista de las categorías
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<CategoryProduct>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAll());
    }

    /**
     * Crea una nueva categoría
     * @param category - Categoría con los datos a crear
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> createCategory(@RequestBody CategoryProduct category,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            categoryService.save(category);
            response.put("message", "Categoría registrada correctamente");
            recordService.registerAction(userDetails, "CREATE", "categoría creada: " + category.getName());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message",e.getMessage());
            recordService.registerAction(userDetails, "CREATE_FAILURE",
                    "ERROR: crear categoría(" + category.getName() + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
