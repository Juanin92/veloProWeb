package com.veloProWeb.controller.Purchase;

import com.veloProWeb.model.dto.purchase.SupplierRequestDTO;
import com.veloProWeb.model.dto.purchase.SupplierResponseDTO;
import com.veloProWeb.service.Purchase.Interfaces.ISupplierService;
import com.veloProWeb.service.Record.IRecordService;
import com.veloProWeb.util.ResponseMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/proveedores")
@AllArgsConstructor
@Validated
public class SupplierController {

    private final ISupplierService supplierService;
    private final IRecordService recordService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<SupplierResponseDTO>> getListSupplier(){
        return ResponseEntity.ok(supplierService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> createSupplier(@RequestBody @Valid SupplierRequestDTO supplier,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        supplierService.createSupplier(supplier);
        recordService.registerAction(userDetails, "CREATE",
                String.format("Proveedor %s Creado", supplier.getName()));
        return new ResponseEntity<>(ResponseMessage.message("Proveedor creado exitosamente!"), HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> updateSupplier(@RequestBody @Valid SupplierRequestDTO supplier,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        supplierService.updateSupplier(supplier);
        recordService.registerAction(userDetails, "UPDATE",
                String.format("Proveedor %s actualizado", supplier.getName()));
        return new ResponseEntity<>(ResponseMessage.message("Datos actualizado exitosamente!"), HttpStatus.OK);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<SupplierResponseDTO> getSupplierByRut(@RequestParam
                                                                @NotBlank(message = "El rut es obligatorio")
                                                                @Pattern(regexp = "^\\d{7,8}-[\\dKk]$",
                                                                        message = "El rut no tiene un formato válido")
                                                                String rut) {
        return new ResponseEntity<>(supplierService.getDtoByRut(rut), HttpStatus.OK);
    }
}
