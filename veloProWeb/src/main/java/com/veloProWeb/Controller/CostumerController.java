package com.veloProWeb.Controller;

import com.veloProWeb.Model.Entity.Costumer.Costumer;
import com.veloProWeb.Service.Costumer.ICostumerService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PutMapping("/update")
    public ResponseEntity<Costumer> updateCostumer(@RequestBody Costumer costumer){
        try {
            costumerService.updateCostumer(costumer);
            return ResponseEntity.ok(costumer);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }
}
