package com.veloproweb.controller.product;

import com.veloproweb.model.entity.product.CategoryProduct;
import com.veloproweb.service.product.interfaces.ICategoryService;
import com.veloproweb.service.reporting.interfaces.IRecordService;
import com.veloproweb.util.ResponseMessage;
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
@RequestMapping("/categoria")
@AllArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;
    private final IRecordService recordService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<CategoryProduct>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> createCategory(@RequestBody @Valid CategoryProduct category,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        categoryService.save(category);
        recordService.registerAction(userDetails, "CREATE", "categoría creada: " + category.getName());
        return new ResponseEntity<>(ResponseMessage.message("Categoría registrada correctamente"),
                HttpStatus.CREATED);
    }
}
