package com.veloProWeb.Controller.Product;

import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Service.Product.Interfaces.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar operaciones relacionadas con los productos.
 * Este controlador proporciona endpoints para obtener todos los productos y crear nuevos productos.
 */
@RestController
@RequestMapping("/stock")
@CrossOrigin(origins = "http://localhost:4200")
public class StockController {

    @Autowired private IProductService productService;

    /**
     * Obtiene una lista de todas los productos
     * @return - ResponseEntity con una lista de los productos
     */
    @GetMapping()
    public ResponseEntity<List<Product>> getListProducts(){
        return ResponseEntity.ok(productService.getAll());
    }

    /**
     * Crea un nuevo producto
     * @param product - Producto con los datos a crear
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping()
    public ResponseEntity<Map<String, String>> createProduct(@RequestBody Product product){
        Map<String, String> response =  new HashMap<>();
        try{
            productService.create(product);
            response.put("message","Producto creado exitosamente!");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
