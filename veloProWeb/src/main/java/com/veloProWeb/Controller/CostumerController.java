package com.veloProWeb.Controller;

import com.veloProWeb.Model.Entity.Costumer.Costumer;
import com.veloProWeb.Service.Costumer.ICostumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class CostumerController {

    @Autowired private ICostumerService costumerService;

    @GetMapping
    public List<Costumer> getListCostumer(){
        return costumerService.getAll();
    }

    @PostMapping("/add")
    public ResponseEntity<String> addCostumer(@RequestBody Costumer costumer){
        costumerService.addNewCostumer(costumer);
        return ResponseEntity.ok("Cliente agregado correctamente!");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateCostumer(@RequestBody Costumer costumer){
        try {
            costumerService.updateCostumer(costumer);
            return ResponseEntity.ok("Cliente actualizado correctamente!");
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
