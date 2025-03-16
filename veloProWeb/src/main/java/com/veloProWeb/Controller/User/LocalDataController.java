package com.veloProWeb.Controller.User;

import com.veloProWeb.Model.Entity.User.LocalData;
import com.veloProWeb.Service.Record.IRecordService;
import com.veloProWeb.Service.User.Interface.ILocalDataService;
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
@RequestMapping("/data")
@CrossOrigin(origins = "http://localhost:4200")
public class LocalDataController {

    @Autowired private ILocalDataService localDataService;
    @Autowired private IRecordService recordService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public List<LocalData> getData(){
        return localDataService.getData();
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public ResponseEntity<Map<String, String>> saveData(@RequestBody LocalData data,
                                                        @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            localDataService.saveData(data);
            response.put("message", "Información registrada");
            recordService.registerAction(userDetails, "CREATE", String.format("Creación datos de la empresa %s",
                    data.getName()));
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            recordService.registerAction(userDetails, "CREATE_FAILURE",
                    String.format("Error: crear datos empresa %s (%s)", data.getName(), e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public ResponseEntity<Map<String, String>> updateData(@RequestBody LocalData data,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            localDataService.updateData(data);
            response.put("message", "Información actualizada");
            recordService.registerAction(userDetails, "UPDATE", String.format("Actualiza datos de la empresa %s",
                    data.getName()));
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            recordService.registerAction(userDetails, "UPDATE_FAILURE",
                    String.format("Error: actualizar datos empresa %s (%s)", data.getName(), e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
