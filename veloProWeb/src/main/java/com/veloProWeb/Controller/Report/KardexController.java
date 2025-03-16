package com.veloProWeb.Controller.Report;

import com.veloProWeb.Model.Entity.Kardex;
import com.veloProWeb.Service.Record.IRecordService;
import com.veloProWeb.Service.Report.IkardexService;
import org.springframework.beans.factory.annotation.Autowired;
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
@CrossOrigin(origins = "http://localhost:4200")
public class KardexController {
    @Autowired private IkardexService kardexService;
    @Autowired private IRecordService recordService;

    /**
     * Obtener todos los registros
     * @return - ResponseEntity emite una lista de los registros
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<Kardex>> getAllKardex(@AuthenticationPrincipal UserDetails userDetails){
        try{
            recordService.registerAction(userDetails, "VIEW_KARDEX", "Observo Reporte producto");
            return ResponseEntity.ok(kardexService.getAll());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
