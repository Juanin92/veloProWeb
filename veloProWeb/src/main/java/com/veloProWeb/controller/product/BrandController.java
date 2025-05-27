package com.veloProWeb.controller.product;

import com.veloProWeb.model.entity.product.BrandProduct;
import com.veloProWeb.service.product.interfaces.IBrandService;
import com.veloProWeb.service.reporting.interfaces.IRecordService;
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
@RequestMapping("/marcas")
@AllArgsConstructor
public class BrandController {

    private final IBrandService brandService;
    private final IRecordService recordService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<BrandProduct>> getAllBrands(){
        return ResponseEntity.ok(brandService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> createBrand(@RequestBody @Valid BrandProduct brand,
                                                           @AuthenticationPrincipal UserDetails userDetails){
        brandService.save(brand);
        recordService.registerAction(userDetails, "CREATE", "Marca creada: " + brand.getName());
        return new ResponseEntity<>(ResponseMessage.message("Marca registrada correctamente"),
                HttpStatus.CREATED);
    }
}
