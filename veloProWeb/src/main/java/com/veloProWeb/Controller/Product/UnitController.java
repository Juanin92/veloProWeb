package com.veloProWeb.Controller.Product;

import com.veloProWeb.Model.Entity.Product.UnitProduct;
import com.veloProWeb.Service.Product.Interfaces.IUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * Obtiene una lista de todas las unidades de medidas
     * @return - ResponseEntity con una lista de las unidades
     */
    @GetMapping
    public ResponseEntity<List<UnitProduct>> getAllUnits(){
        return ResponseEntity.ok(unitService.getAll());
    }

    /**
     * Crea una nueva unidad
     * @param unit - Unidad con los datos a crear
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createUnit(@RequestBody UnitProduct unit){
        Map<String, String> response = new HashMap<>();
        try {
            unitService.save(unit);
            response.put("message", "Unidad de medida registrada correctamente");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
