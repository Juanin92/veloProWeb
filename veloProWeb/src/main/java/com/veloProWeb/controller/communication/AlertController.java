package com.veloProWeb.controller.communication;

import com.veloProWeb.model.entity.communication.Alert;
import com.veloProWeb.service.reporting.interfaces.IRecordService;
import com.veloProWeb.service.communication.interfaces.IAlertService;
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
@RequestMapping("/alertas")
@CrossOrigin(origins = "http://localhost:4200")
public class AlertController {

    @Autowired private IAlertService alertService;
    @Autowired private IRecordService recordService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<Alert>> getAlerts(){
        try {
            return ResponseEntity.ok(alertService.getAlerts());
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> handleStatus(@RequestBody Alert alert, @RequestParam int action,
                                                            @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            response.put("message", "Cambio de status correcto de la alerta");
            alertService.handleAlertStatus(alert, action);
            recordService.registerAction(userDetails, "UPDATE",
                    String.format("Actualizar estado de la alerta %s (%s) ", alert.getDescription(), alert.getStatus()));
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            recordService.registerAction(userDetails, "UPDATE_FAILURE",
                    String.format("Error: actualizar estado de la alerta %s (%s) - %s ", alert.getDescription(),
                            alert.getStatus(), e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
