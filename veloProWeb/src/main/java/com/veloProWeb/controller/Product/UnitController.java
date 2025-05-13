package com.veloProWeb.controller.Product;

import com.veloProWeb.model.entity.Product.UnitProduct;
import com.veloProWeb.service.Product.Interfaces.IUnitService;
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
@RequestMapping("/unidad")
@AllArgsConstructor
public class UnitController {

    private final IUnitService unitService;
    private final IRecordService recordService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<UnitProduct>> getAllUnits(){
        return ResponseEntity.ok(unitService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> createUnit(@RequestBody @Valid UnitProduct unit,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        unitService.save(unit);
        recordService.registerAction(userDetails, "CREATE", "unidad de medida creada: " + unit.getNameUnit());
        return new ResponseEntity<>(ResponseMessage.message("Unidad de medida registrada correctamente"),
                HttpStatus.CREATED);
    }
}
