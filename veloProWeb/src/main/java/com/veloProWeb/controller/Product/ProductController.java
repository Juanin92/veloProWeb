package com.veloProWeb.controller.Product;

import com.veloProWeb.model.dto.product.ProductRequestDTO;
import com.veloProWeb.model.dto.product.ProductResponseDTO;
import com.veloProWeb.model.dto.product.ProductUpdatedRequestDTO;
import com.veloProWeb.model.entity.Product.Product;
import com.veloProWeb.service.Product.Interfaces.IProductService;
import com.veloProWeb.service.Record.IRecordService;
import com.veloProWeb.util.ResponseMessage;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ProductController {

    private final IProductService productService;
    private final IRecordService recordService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER','SELLER', 'WAREHOUSE', 'GUEST')")
    public ResponseEntity<List<ProductResponseDTO>> getListProducts(){
        return ResponseEntity.ok(productService.getAll());
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> createProduct(@RequestBody @Valid ProductRequestDTO product,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        productService.create(product);
        recordService.registerAction(userDetails, "CREATE",
                String.format("Producto creado: %s", product.getDescription()));
        return new ResponseEntity<>(ResponseMessage.message("Producto creado exitosamente!"), HttpStatus.CREATED);
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
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

    @PutMapping("/eliminar_producto")
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> deleteProduct(@RequestBody @Valid ProductUpdatedRequestDTO product,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        productService.delete(product);
        recordService.registerAction(userDetails, "DELETE",
                String.format("Producto eliminado: %s", product.getDescription()));
        return new ResponseEntity<>(ResponseMessage.message("Producto eliminado exitosamente!"), HttpStatus.OK);
    }

    @PutMapping("/activar_producto")
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> activeProduct(@RequestBody @Valid ProductUpdatedRequestDTO product,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        productService.active(product);
        recordService.registerAction(userDetails, "ACTIVATE",
                String.format("Producto activado: %S", product.getDescription()));
        return new ResponseEntity<>(ResponseMessage.message("Producto activado exitosamente!"), HttpStatus.OK);
    }
}
