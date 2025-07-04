package com.veloproweb.controller.inventory;

import com.veloproweb.model.dto.inventory.KardexResponseDTO;
import com.veloproweb.service.reporting.interfaces.IRecordService;
import com.veloproweb.service.inventory.IKardexService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kardex")
@AllArgsConstructor
public class KardexController {
    private final IKardexService kardexService;
    private final IRecordService recordService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<KardexResponseDTO>> getAllKardex(@AuthenticationPrincipal UserDetails userDetails){
        recordService.registerAction(userDetails, "VIEW_KARDEX", "Observo Reporte producto");
        return ResponseEntity.ok(kardexService.getAll());
    }
}
