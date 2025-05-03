package com.veloProWeb.controller.Product;

import com.veloProWeb.model.entity.Product.SubcategoryProduct;
import com.veloProWeb.service.Product.Interfaces.ISubcategoryService;
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
 * Controlador REST para gestionar operaciones relacionadas con las subcategorías de productos.
 * Este controlador proporciona endpoints para obtener todas las subcategorías y crear nuevas subcategorías.
 */
@RestController
@RequestMapping("/subcategoria")
@CrossOrigin(origins = "http://localhost:4200")
public class SubcategoryController {

    @Autowired private ISubcategoryService subcategoryService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene una lista de todas las subcategorías
     * @return - ResponseEntity con una lista de las subcategorías
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<SubcategoryProduct>> getAllSubcategories(){
        return ResponseEntity.ok(subcategoryService.getAll());
    }

    /**
     * Obtiene la lista de subcategorías asociadas a una categoría específica por su ID.
     * @param id - Identificador de la categoría cuyas subcategorías se desean obtener.
     * @return - ResponseEntity con una lista de las subcategorías
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<SubcategoryProduct>> getAllSubcategoriesByCategory(@PathVariable Long id){
        return ResponseEntity.ok(subcategoryService.getSubcategoryByCategoryID(id));
    }

    /**
     * Crea una nueva subcategoría
     * @param subcategory - Subcategoría con los datos a crear
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> createSubcategories(@RequestBody SubcategoryProduct subcategory,
                                                                   @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            subcategoryService.save(subcategory);
            response.put("message", "Subcategoría registrada correctamente");
            recordService.registerAction(userDetails, "CREATE", "Subcategoría creada: " + subcategory.getName());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message",e.getMessage());
            recordService.registerAction(userDetails, "CREATE_FAILURE",
                    "ERROR: crear subcategoría(" + subcategory.getName() + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
