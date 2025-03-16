package com.veloProWeb.Controller.Product;

import com.veloProWeb.Model.Entity.Product.CategoryProduct;
import com.veloProWeb.Service.Product.Interfaces.ICategoryService;
import com.veloProWeb.Service.Record.IRecordService;
import com.veloProWeb.Service.User.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Autowired private IUserService userService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene una lista de todas las categorías
     * @return - ResponseEntity con una lista de las categorías
     */
    @GetMapping
    public ResponseEntity<List<CategoryProduct>> getAllCategories(@AuthenticationPrincipal UserDetails userDetails){
        if (userService.hasRequiredRole(userDetails, "ADMIN", "MASTER")){
            return ResponseEntity.ok(categoryService.getAll());
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Crea una nueva categoría
     * @param category - Categoría con los datos a crear
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createCategory(@RequestBody CategoryProduct category,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            if(userService.hasRequiredRole(userDetails, "ADMIN", "MASTER")){
                categoryService.save(category);
                response.put("message", "Categoría registrada correctamente");
                recordService.registerAction(userDetails, "CREATE", "categoría creada: " + category.getName());
                return ResponseEntity.ok(response);
            }else {
                recordService.registerAction(userDetails, "CREATE_FAILURE",
                        "Error: " + userDetails.getUsername() + " ingreso indebido");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }catch (IllegalArgumentException e){
            response.put("message",e.getMessage());
            recordService.registerAction(userDetails, "CREATE_FAILURE",
                    "ERROR: crear categoría(" + category.getName() + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
