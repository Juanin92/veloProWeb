package com.veloProWeb.Controller.Report;

import com.veloProWeb.Model.DTO.KardexRequestDTO;
import com.veloProWeb.Model.Entity.Kardex;
import com.veloProWeb.Service.Report.IkardexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar operaciones relacionadas con lso registros/movimientos del inventario.
 * Este controlador proporciona endpoints para obtener listas y creaciones de registros.
 */
@RestController
@RequestMapping("/kardex")
@CrossOrigin(origins = "http://localhost:4200")
public class KardexController {
    @Autowired private IkardexService kardexService;

    /**
     * Obtener todos los registros
     * @return - ResponseEntity emite una lista de los registros
     */
    @GetMapping
    public ResponseEntity<List<Kardex>> getAllKardex(){
        try{
            return ResponseEntity.ok(kardexService.getAll());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Crear un registro de kardex
     * @param dto - Objeto con los datos necesarios
     * @return - ResponseEntity emite un mensaje de confirmación o error del proceso
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createKardex(@RequestBody KardexRequestDTO dto){
        Map<String, String> response = new HashMap<>();
        try{
            response.put("Ok","Registro registrado");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
