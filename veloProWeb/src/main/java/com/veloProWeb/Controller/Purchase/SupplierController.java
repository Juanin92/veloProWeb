package com.veloProWeb.Controller.Purchase;

import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Service.Purchase.Interfaces.ISupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/proveedores")
@CrossOrigin(origins = "http://localhost:4200")
public class SupplierController {

    @Autowired private ISupplierService supplierService;

    @GetMapping
    public ResponseEntity<List<Supplier>> getListSupplier(){
        return ResponseEntity.ok(supplierService.getAll());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createSupplier(@RequestBody Supplier supplier){
        Map<String, String> response = new HashMap<>();
        try{
            supplierService.createSupplier(supplier);
            response.put("message","Proveedor creado exitosamente!");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
