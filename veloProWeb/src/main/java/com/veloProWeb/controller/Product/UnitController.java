package com.veloProWeb.controller.Product;

import com.veloProWeb.model.entity.Product.UnitProduct;
import com.veloProWeb.service.Product.Interfaces.IUnitService;
import com.veloProWeb.service.Record.IRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar operaciones relacionadas con las unidades de medidas de productos.
 * Este controlador proporciona endpoints para obtener todas las unidades y crear nuevas unidades.
 */
@RestController
@RequestMapping("/unidad")
@CrossOrigin(origins = "http://localhost:4200")
public class UnitController {

    @Autowired private IUnitService unitService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene una lista de todas las unidades de medidas
     * @return - ResponseEntity con una lista de las unidades
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<UnitProduct>> getAllUnits(){
        return ResponseEntity.ok(unitService.getAll());
    }

    /**
     * Crea una nueva unidad
     * @param unit - Unidad con los datos a crear
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> createUnit(@RequestBody UnitProduct unit,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            unitService.save(unit);
            response.put("message", "Unidad de medida registrada correctamente");
            recordService.registerAction(userDetails, "CREATE", "unidad creada: " + unit.getNameUnit());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message",e.getMessage());
            recordService.registerAction(userDetails, "CREATE_FAILURE",
                    "ERROR: crear unidad(" + unit.getNameUnit() + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
