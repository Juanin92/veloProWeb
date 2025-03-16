package com.veloProWeb.Controller.Purchase;

import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Service.Purchase.Interfaces.ISupplierService;
import com.veloProWeb.Service.Record.IRecordService;
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
import java.util.Optional;

/**
 * Controlador REST para gestionar operaciones relacionadas con proveedores.
 * Este controlador proporciona endpoints para listar, agregar, buscar, actualizar proveedores.
 */
@RestController
@RequestMapping("/proveedores")
@CrossOrigin(origins = "http://localhost:4200")
public class SupplierController {

    @Autowired private ISupplierService supplierService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene una lista de proveedores.
     * @return - ResponseEntity con una lista de proveedores
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<Supplier>> getListSupplier(){
        return ResponseEntity.ok(supplierService.getAll());
    }

    /**
     * Agrega un nuevo proveedor
     * @param supplier - Proveedor con los datos que se desea agregar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> createSupplier(@RequestBody Supplier supplier,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            supplierService.createSupplier(supplier);
            response.put("message","Proveedor creado exitosamente!");
            recordService.registerAction(userDetails, "CREATE", "Proveedor Creado: " + supplier.getName());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            recordService.registerAction(userDetails, "CREATE_FAILURE", "Error: crear proveedor: " + supplier.getName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Actualiza los datos de un proveedor
     * @param supplier - Proveedor con los datos que se desea actualizar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> updateSupplier(@RequestBody Supplier supplier,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            supplierService.updateSupplier(supplier);
            response.put("message","Datos actualizado exitosamente!");
            recordService.registerAction(userDetails, "UPDATE", "Proveedor actualizado: " + supplier.getName());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            recordService.registerAction(userDetails, "UPDATE_FAILURE", "Error: actualizar proveedor: " + supplier.getName());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Obtener un proveedor por un ID
     * @param id - Identificador de proveedor a buscar
     * @return - ResponseEntity con el proveedor o Not_found al no obtener nada
     */
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Supplier> getSupplierById(@RequestParam Long id){
        Optional<Supplier> supplier = supplierService.getSupplierById(id);
        return supplier.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
