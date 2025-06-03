package com.veloProWeb.controller.inventory;

import com.veloProWeb.model.dto.inventory.KardexResponseDTO;
import com.veloProWeb.service.reporting.interfaces.IRecordService;
import com.veloProWeb.service.inventory.IKardexService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar operaciones relacionadas con lso registros/movimientos del inventario.
 * Este controlador proporciona endpoints para obtener listas de registros.
 */
@RestController
@RequestMapping("/kardex")
@AllArgsConstructor
public class KardexController {
    private final IKardexService kardexService;
    private final IRecordService recordService;

    /**
     * Obtener todos los registros
     * @return - ResponseEntity emite una lista de los registros
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<KardexResponseDTO>> getAllKardex(@AuthenticationPrincipal UserDetails userDetails){
        try{
            recordService.registerAction(userDetails, "VIEW_KARDEX", "Observo Reporte producto");
            return ResponseEntity.ok(kardexService.getAll());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
