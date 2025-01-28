package com.veloProWeb.Controller.Report;

import com.veloProWeb.Model.Entity.Kardex;
import com.veloProWeb.Service.Report.IkardexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
