package com.veloProWeb.controller.product;

import com.veloProWeb.model.dto.product.ProductRequestDTO;
import com.veloProWeb.model.dto.product.ProductResponseDTO;
import com.veloProWeb.model.dto.product.ProductUpdatedRequestDTO;
import com.veloProWeb.service.product.interfaces.IProductService;
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
        productService.create(product, userDetails);
        recordService.registerAction(userDetails, "CREATE",
                String.format("Producto creado: %s", product.getDescription()));
        return new ResponseEntity<>(ResponseMessage.message("Producto creado exitosamente!"), HttpStatus.CREATED);
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> updateProduct(@RequestBody @Valid ProductUpdatedRequestDTO product,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        productService.updateProductInfo(product, userDetails);
        recordService.registerAction(userDetails, "UPDATE",
                String.format("Producto actualizado: %s", product.getDescription()));
        return new ResponseEntity<>(ResponseMessage.message("Producto actualizado exitosamente!"), HttpStatus.OK);
    }

    @PutMapping("/eliminar_producto")
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> deleteProduct(@RequestBody @Valid ProductUpdatedRequestDTO product,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        productService.discontinueProduct(product, userDetails);
        recordService.registerAction(userDetails, "DELETE",
                String.format("Producto eliminado: %s", product.getDescription()));
        return new ResponseEntity<>(ResponseMessage.message("Producto eliminado exitosamente!"), HttpStatus.OK);
    }

    @PutMapping("/activar_producto")
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> activeProduct(@RequestBody @Valid ProductUpdatedRequestDTO product,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        productService.reactive(product, userDetails);
        recordService.registerAction(userDetails, "ACTIVATE",
                String.format("Producto activado: %S", product.getDescription()));
        return new ResponseEntity<>(ResponseMessage.message("Producto activado exitosamente!"), HttpStatus.OK);
    }
}
