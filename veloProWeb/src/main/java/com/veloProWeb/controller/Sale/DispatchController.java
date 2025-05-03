package com.veloProWeb.controller.Sale;

import com.veloProWeb.model.dto.DetailSaleRequestDTO;
import com.veloProWeb.model.dto.DispatchDTO;
import com.veloProWeb.model.entity.Sale.Dispatch;
import com.veloProWeb.service.Record.IRecordService;
import com.veloProWeb.service.Sale.Interface.IDispatchService;
import com.veloProWeb.service.Sale.Interface.ISaleDetailService;
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

@RestController
@RequestMapping("/despachos")
@CrossOrigin(origins = "http://localhost:4200")
public class DispatchController {

    @Autowired private IDispatchService dispatchService;
    @Autowired private ISaleDetailService saleDetailService;
    @Autowired private IRecordService recordService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'GUEST', 'SELLER')")
    public ResponseEntity<List<DispatchDTO>> getDispatches(){
        try{
            return ResponseEntity.ok(dispatchService.getDispatches());
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'GUEST', 'SELLER')")
    public ResponseEntity<Map<String, String>> createDispatch(@RequestBody DispatchDTO dto,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try {
            Dispatch dispatch = dispatchService.createDispatch(dto);
            saleDetailService.createSaleDetailsToDispatch(dto.getDetailSaleDTOS(), dispatch);
            recordService.registerAction(userDetails, "CREATE", "Despacho creado: " + dto.getCustomer());
            response.put("message", "Despacho en preparación");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            recordService.registerAction(userDetails, "CREATE_FAILURE",
                    "Error: crear despacho:  " + dto.getCustomer() + " / " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'SELLER')")
    public ResponseEntity<Map<String, String>> handleStatusDispatch(@RequestParam Long dispatchID, @RequestParam int action,
                                                                    @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try {
            response.put("message", "Cambio de status del despacho");
            dispatchService.handleStatus(dispatchID, action);
            recordService.registerAction(userDetails, "UPDATE", "Actualiza estado del despacho " + dispatchID);
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            recordService.registerAction(userDetails, "UPDATE_FAILURE",
                    "Actualiza estado del despacho " + dispatchID + " (" + e.getMessage() + ")");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/detalles")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'GUEST', 'SELLER')")
    public ResponseEntity<List<DetailSaleRequestDTO>> getDetailSale(@RequestParam Long idDispatch){
        try{
            return ResponseEntity.ok(saleDetailService.getSaleDetailsToDispatch(idDispatch));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
