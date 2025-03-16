package com.veloProWeb.Controller.Product;

import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Service.Product.Interfaces.IProductService;
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

@RestController
@RequestMapping("/stock")
@CrossOrigin(origins = "http://localhost:4200")
public class StockController {

    @Autowired private IProductService productService;
    @Autowired private IUserService userService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene una lista de todas los productos
     * @return - ResponseEntity con una lista de los productos
     */
    @GetMapping()
    public ResponseEntity<List<Product>> getListProducts(@AuthenticationPrincipal UserDetails userDetails){
        if (userService.hasRequiredRole(userDetails, "ADMIN", "MASTER", "SELLER")){
            return ResponseEntity.ok(productService.getAll());
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Crea un nuevo producto
     * @param product - Producto con los datos a crear
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping()
    public ResponseEntity<Map<String, String>> createProduct(@RequestBody Product product,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response =  new HashMap<>();
        try{
            if(userService.hasRequiredRole(userDetails, "ADMIN", "MASTER")){
                productService.create(product);
                response.put("message","Producto creado exitosamente!");
                recordService.registerAction(userDetails, "CREATE", "Producto creado: " + product.getDescription());
                return ResponseEntity.ok(response);
            }else {
                recordService.registerAction(userDetails, "CREATE_FAILURE",
                        "Error: " + userDetails.getUsername() + " ingreso indebido");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
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
    public ResponseEntity<Map<String, String>> updateProduct(@RequestBody Product product,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            if(userService.hasRequiredRole(userDetails, "ADMIN", "MASTER")){
                productService.update(product);
                response.put("message", "Producto actualizado exitosamente!");
                recordService.registerAction(userDetails, "UPDATE", "Producto actualizado: " + product.getDescription());
                return ResponseEntity.ok(response);
            }else {
                recordService.registerAction(userDetails, "UPDATE_FAILURE",
                        "Error: " + userDetails.getUsername() + " ingreso indebido");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
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
    public ResponseEntity<Map<String, String>> deleteProduct(@RequestBody Product product,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            if(userService.hasRequiredRole(userDetails, "ADMIN", "MASTER")){
                productService.delete(product);
                response.put("message", "Producto eliminado exitosamente!");
                recordService.registerAction(userDetails, "DELETE", "Producto eliminado: " + product.getDescription());
                return ResponseEntity.ok(response);
            }else {
                recordService.registerAction(userDetails, "DELETE_FAILURE",
                        "Error: " + userDetails.getUsername() + " ingreso indebido");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
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
    public ResponseEntity<Map<String, String>> activeProduct(@RequestBody Product product,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            if(userService.hasRequiredRole(userDetails, "ADMIN", "MASTER")){
                productService.active(product);
                response.put("message", "Producto activado exitosamente!");
                recordService.registerAction(userDetails, "ACTIVATE", "Producto activado: " + product.getDescription());
                return ResponseEntity.ok(response);
            }else {
                recordService.registerAction(userDetails, "ACTIVATE_FAILURE",
                        "Error: " + userDetails.getUsername() + " ingreso indebido");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }catch (IllegalArgumentException e){
            response.put("message",e.getMessage());
            recordService.registerAction(userDetails, "ACTIVATE_FAILURE",
                    "ERROR: activar producto(" + product.getDescription() + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
