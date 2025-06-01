package com.veloProWeb.controller.Sale;

import com.veloProWeb.model.dto.sale.CashRegisterRequestDTO;
import com.veloProWeb.model.dto.sale.CashRegisterResponseDTO;
import com.veloProWeb.service.reporting.interfaces.IRecordService;
import com.veloProWeb.service.sale.Interface.ICashRegisterService;
import com.veloProWeb.util.ResponseMessage;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/caja")
@AllArgsConstructor
public class CashRegisterController {

    private final ICashRegisterService cashRegisterService;
    private final IRecordService recordService;

    /**
     * Obtiene una lista con los registros de caja
     * @return - lista con los registros
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public  ResponseEntity<List<CashRegisterResponseDTO>> getCashRegisters(@AuthenticationPrincipal UserDetails userDetails){
        recordService.registerAction(userDetails, "VIEW", "Lista de registro de la caja");
        return ResponseEntity.ok(cashRegisterService.getAll());
    }

    @GetMapping("/verificar-apertura")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'SELLER', 'GUEST')")
    public ResponseEntity<Map<String, Boolean>> hasOpenRegisterOnDate(@AuthenticationPrincipal UserDetails userDetails){
        boolean result = cashRegisterService.hasOpenRegisterOnDate(userDetails.getUsername());
        Map<String, Boolean> response = Collections.singletonMap("isOpen", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/apertura")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'SELLER', 'GUEST')")
    public ResponseEntity<Map<String, String>> addRegisterOpening(@AuthenticationPrincipal UserDetails userDetails,
                                                                  @RequestBody int amount){
        cashRegisterService.openRegister(userDetails.getUsername(), amount);
        recordService.registerAction(userDetails, "OPEN", String.format("Apertura de caja con $%s pesos", amount));
        return new ResponseEntity<>(ResponseMessage.message("Apertura de caja exitosa"), HttpStatus.CREATED);
    }

    @PostMapping("/cierre")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'SELLER', 'GUEST')")
    public ResponseEntity<Map<String, String>> addRegisterClosing(@AuthenticationPrincipal UserDetails userDetails,
                                                                  @RequestBody CashRegisterRequestDTO dto){
        cashRegisterService.closeRegister(userDetails.getUsername(), dto);
        recordService.registerAction(userDetails, "CLOSE", String.format("Cierre de caja: $%s pesos - " +
                "POS: $%s pesos", dto.getAmountClosingCash(), dto.getAmountClosingPos()));
        return new ResponseEntity<>(ResponseMessage.message("Cierre de caja exitoso"), HttpStatus.CREATED);
    }
}
