package com.veloProWeb.Controller;

import com.veloProWeb.Model.Entity.Costumer.Costumer;
import com.veloProWeb.Service.Costumer.ICostumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class CostumerController {

    @Autowired private ICostumerService costumerService;

    @GetMapping
    public List<Costumer> getListCostumer(){
        return costumerService.getAll();
    }

    @PostMapping("/agregar")
    public ResponseEntity<Map<String, String>> addCostumer(@RequestBody Costumer costumer){
        Map<String, String> response = new HashMap<>();
        try {
            costumerService.addNewCostumer(costumer);
            response.put("message","Cliente agregado correctamente!");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> updateCostumer(@RequestBody Costumer costumer){
        try {
            costumerService.updateCostumer(costumer);
            return ResponseEntity.ok("{\"message\":\"Cliente actualizado correctamente!\"}");
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/eliminar")
    public ResponseEntity<String> deleteCostumer(@RequestBody Costumer costumer){
        try {
            costumerService.delete(costumer);
            return ResponseEntity.ok("{\"message\":\"Cliente eliminado correctamente!\"}");
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
