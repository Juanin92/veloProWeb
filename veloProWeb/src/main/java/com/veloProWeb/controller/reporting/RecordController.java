package com.veloProWeb.controller.reporting;

import com.veloProWeb.model.dto.reporting.RecordResponseDTO;
import com.veloProWeb.service.reporting.interfaces.IRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registro")
@AllArgsConstructor
public class RecordController {

    private final IRecordService recordService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('MASTER')")
    public ResponseEntity<List<RecordResponseDTO>> getAllRecord(){
        return ResponseEntity.ok(recordService.getAllRecord());
    }
}
