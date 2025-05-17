package com.veloProWeb.controller.product;

import com.veloProWeb.model.entity.product.SubcategoryProduct;
import com.veloProWeb.service.product.interfaces.ISubcategoryService;
import com.veloProWeb.service.Record.IRecordService;
import com.veloProWeb.util.ResponseMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/subcategoria")
@AllArgsConstructor
@Validated
public class SubcategoryController {

    private final ISubcategoryService subcategoryService;
    private final IRecordService recordService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<SubcategoryProduct>> getAllSubcategoriesByCategory(@PathVariable @NotNull(
            message = "El ID no puede estar vacío") Long id){
        return ResponseEntity.ok(subcategoryService.getSubcategoryByCategoryID(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> createSubcategories(@RequestBody @Valid SubcategoryProduct subcategory,
                                                                   @AuthenticationPrincipal UserDetails userDetails){
        subcategoryService.save(subcategory);
        recordService.registerAction(userDetails, "CREATE",
                String.format("Subcategoría creada: %s", subcategory.getName()));
        return new ResponseEntity<>(ResponseMessage.message("Subcategoría registrada correctamente"),
                HttpStatus.CREATED);
    }
}
