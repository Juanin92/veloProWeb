package com.veloProWeb.controller.data;

import com.veloProWeb.model.dto.data.LocalDataDTO;
import com.veloProWeb.service.reporting.interfaces.IRecordService;
import com.veloProWeb.service.data.ILocalDataService;
import com.veloProWeb.util.ResponseMessage;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/data")
@AllArgsConstructor
public class LocalDataController {

    private final ILocalDataService localDataService;
    private final IRecordService recordService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'SELLER', 'WAREHOUSE', 'GUEST')")
    public ResponseEntity<LocalDataDTO> getData(){
        return ResponseEntity.ok(localDataService.getData());
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public ResponseEntity<Map<String, String>> updateData(@RequestBody @Valid LocalDataDTO data,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        localDataService.updateData(data);
        recordService.registerAction(userDetails, "UPDATE", String.format("Actualiza datos de la empresa %s",
                data.getName()));
        return ResponseEntity.ok(ResponseMessage.message("Información actualizada"));
    }
}
