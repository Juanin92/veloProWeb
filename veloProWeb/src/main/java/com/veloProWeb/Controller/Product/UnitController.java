package com.veloProWeb.Controller.Product;

import com.veloProWeb.Model.Entity.Product.CategoryProduct;
import com.veloProWeb.Model.Entity.Product.SubcategoryProduct;
import com.veloProWeb.Model.Entity.Product.UnitProduct;
import com.veloProWeb.Service.Product.Interfaces.IUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/unidad")
@CrossOrigin(origins = "http://localhost:4200")
public class UnitController {

    @Autowired private IUnitService unitService;

    @GetMapping
    public ResponseEntity<List<UnitProduct>> getAllUnits(){
        return ResponseEntity.ok(unitService.getAll());
    }

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
