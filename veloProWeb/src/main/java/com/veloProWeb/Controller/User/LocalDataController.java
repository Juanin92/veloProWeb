package com.veloProWeb.Controller.User;

import com.veloProWeb.Model.Entity.User.LocalData;
import com.veloProWeb.Service.User.Interface.ILocalDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/data")
@CrossOrigin(origins = "http://localhost:4200")
public class LocalDataController {

    @Autowired private ILocalDataService localDataService;

    @GetMapping
    public List<LocalData> getData(){
        return localDataService.getData();
    }

    @PostMapping()
    public ResponseEntity<Map<String, String>> saveData(@RequestBody LocalData data){
        Map<String, String> response = new HashMap<>();
        try{
            localDataService.saveData(data);
            response.put("message", "Información registrada");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping()
    public ResponseEntity<Map<String, String>> updateData(@RequestBody LocalData data){
        Map<String, String> response = new HashMap<>();
        try{
            localDataService.updateData(data);
            response.put("message", "Información actualizada");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
