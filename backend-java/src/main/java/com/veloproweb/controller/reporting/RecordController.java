package com.veloproweb.controller.reporting;

import com.veloproweb.model.dto.reporting.RecordResponseDTO;
import com.veloproweb.service.reporting.interfaces.IRecordService;
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
