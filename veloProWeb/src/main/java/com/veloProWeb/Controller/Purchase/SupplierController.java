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
import java.util.Optional;

/**
 * Controlador REST para gestionar operaciones relacionadas con proveedores.
 * Este controlador proporciona endpoints para listar, agregar, actualizar proveedores.
 */
@RestController
@RequestMapping("/proveedores")
@CrossOrigin(origins = "http://localhost:4200")
public class SupplierController {

    @Autowired private ISupplierService supplierService;

    /**
     * Obtiene una lista de proveedores.
     * @return - ResponseEntity con una lista de proveedores
     */
    @GetMapping
    public ResponseEntity<List<Supplier>> getListSupplier(){
        return ResponseEntity.ok(supplierService.getAll());
    }

    /**
     * Agrega un nuevo proveedor
     * @param supplier - Proveedor con los datos que se desea agregar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
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

    /**
     * Actualiza los datos de un proveedor
     * @param supplier - Proveedor con los datos que se desea actualizar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping
    public ResponseEntity<Map<String, String>> updateSupplier(@RequestBody Supplier supplier){
        Map<String, String> response = new HashMap<>();
        try{
            supplierService.updateSupplier(supplier);
            response.put("message","Datos actualizado exitosamente!");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<Supplier> getSupplierById(@RequestParam Long id){
        Optional<Supplier> supplier = supplierService.getSupplierById(id);
        return supplier.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
