package com.veloProWeb.Controller.Product;

import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Service.Product.Interfaces.IProductService;
import com.veloProWeb.Service.Record.IRecordService;
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

@RestController
@RequestMapping("/stock")
@CrossOrigin(origins = "http://localhost:4200")
public class StockController {

    @Autowired private IProductService productService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene una lista de todas los productos
     * @return - ResponseEntity con una lista de los productos
     */
    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER','SELLER')")
    public ResponseEntity<List<Product>> getListProducts(){
        return ResponseEntity.ok(productService.getAll());
    }

    /**
     * Crea un nuevo producto
     * @param product - Producto con los datos a crear
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER')")
    public ResponseEntity<Map<String, String>> createProduct(@RequestBody Product product,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response =  new HashMap<>();
        try{
            productService.create(product);
            response.put("message","Producto creado exitosamente!");
            recordService.registerAction(userDetails, "CREATE", "Producto creado: " + product.getDescription());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message",e.getMessage());
            recordService.registerAction(userDetails, "CREATE_FAILURE",
                    "ERROR: crear producto(" + product.getDescription() + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Actualizar un producto seleccionado
     * @param product - Producto con los valores actualizar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER')")
    public ResponseEntity<Map<String, String>> updateProduct(@RequestBody Product product,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            productService.update(product);
            response.put("message", "Producto actualizado exitosamente!");
            recordService.registerAction(userDetails, "UPDATE", "Producto actualizado: " + product.getDescription());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message",e.getMessage());
            recordService.registerAction(userDetails, "UPDATE_FAILURE",
                    "ERROR: actualizar producto(" + product.getDescription() + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Eliminar un producto seleccionado
     * @param product - Producto seleccionado para eliminar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/eliminar_producto")
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER')")
    public ResponseEntity<Map<String, String>> deleteProduct(@RequestBody Product product,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            productService.delete(product);
            response.put("message", "Producto eliminado exitosamente!");
            recordService.registerAction(userDetails, "DELETE", "Producto eliminado: " + product.getDescription());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message",e.getMessage());
            recordService.registerAction(userDetails, "DELETE_FAILURE",
                    "ERROR: eliminado producto(" + product.getDescription() + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Activar un producto seleccionado
     * @param product - Producto seleccionado para activar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/activar_producto")
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER')")
    public ResponseEntity<Map<String, String>> activeProduct(@RequestBody Product product,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            productService.active(product);
            response.put("message", "Producto activado exitosamente!");
            recordService.registerAction(userDetails, "ACTIVATE", "Producto activado: " + product.getDescription());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message",e.getMessage());
            recordService.registerAction(userDetails, "ACTIVATE_FAILURE",
                    "ERROR: activar producto(" + product.getDescription() + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
