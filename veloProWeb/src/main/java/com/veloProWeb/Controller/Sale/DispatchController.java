package com.veloProWeb.Controller.Sale;

import com.veloProWeb.Model.DTO.DetailSaleRequestDTO;
import com.veloProWeb.Model.DTO.DispatchDTO;
import com.veloProWeb.Model.Entity.Sale.Dispatch;
import com.veloProWeb.Service.Sale.Interface.IDispatchService;
import com.veloProWeb.Service.Sale.Interface.ISaleDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping()
    public ResponseEntity<List<DispatchDTO>> getDispatches(){
        try{
            return ResponseEntity.ok(dispatchService.getDispatches());
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createDispatch(@RequestBody DispatchDTO dto){
        Map<String, String> response = new HashMap<>();
        try {
            Dispatch dispatch = dispatchService.createDispatch(dto);
            saleDetailService.createSaleDetailsToDispatch(dto.getDetailSaleDTOS(), dispatch);
            response.put("message", "Despacho en preparación");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping()
    public ResponseEntity<Map<String, String>> handleStatusDispatch(@RequestParam Long dispatchID, @RequestParam int action){
        Map<String, String> response = new HashMap<>();
        try {
            response.put("message", "Cambio de status del despacho");
            dispatchService.handleStatus(dispatchID, action);
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/detalles")
    public ResponseEntity<List<DetailSaleRequestDTO>> getDetailSale(@RequestParam Long idDispatch){
        try{
            return ResponseEntity.ok(saleDetailService.getSaleDetailsToDispatch(idDispatch));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
