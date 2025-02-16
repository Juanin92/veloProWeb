package com.veloProWeb.Controller.User;

import com.veloProWeb.Model.Entity.User.Alert;
import com.veloProWeb.Service.User.Interface.IAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alertas")
@CrossOrigin(origins = "http://localhost:4200")
public class AlertController {

    @Autowired private IAlertService alertService;

    @GetMapping()
    public ResponseEntity<List<Alert>> getAlerts(){
        try {
            return ResponseEntity.ok(alertService.getAlerts());
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping()
    public ResponseEntity<Map<String, String>> handleStatus(@RequestBody Alert alert, @RequestParam int action){
        Map<String, String> response = new HashMap<>();
        try{
            response.put("message", "Cambio de status correcto de la alerta");
            alertService.handleAlertStatus(alert, action);
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
