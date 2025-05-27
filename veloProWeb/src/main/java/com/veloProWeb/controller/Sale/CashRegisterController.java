package com.veloProWeb.controller.Sale;

import com.veloProWeb.model.dto.CashRegisterDTO;
import com.veloProWeb.service.reporting.interfaces.IRecordService;
import com.veloProWeb.service.sale.Interface.ICashRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/caja")
@CrossOrigin(origins = "http://localhost:4200")
public class CashRegisterController {

    @Autowired private ICashRegisterService cashRegisterService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene una lista con los registros de caja
     * @return - lista con los registros
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public List<CashRegisterDTO> getCashRegisters(@AuthenticationPrincipal UserDetails userDetails){
        recordService.registerAction(userDetails, "VIEW", "Lista de registro de la caja");
        return cashRegisterService.getAll();
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
        Map<String, String> response = new HashMap<>();
        try{
            cashRegisterService.addRegisterOpening(userDetails.getUsername(), amount);
            recordService.registerAction(userDetails, "OPEN", String.format("Apertura de caja con $%s pesos", amount));
            response.put("message", "Apertura de caja exitosa");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/cierre")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'SELLER', 'GUEST')")
    public ResponseEntity<Map<String, String>> addRegisterClosing(@AuthenticationPrincipal UserDetails userDetails,
                                                                  @RequestBody CashRegisterDTO dto){
        Map<String, String> response = new HashMap<>();
        try{
            cashRegisterService.addRegisterClosing(userDetails.getUsername(), dto);
            recordService.registerAction(userDetails, "CLOSE", String.format("Cierre de caja: $%s pesos - " +
                    "POS: $%s pesos", dto.getAmountClosingCash(), dto.getAmountClosingPos()));
            response.put("message", "Cierre de caja exitoso");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
