package com.veloProWeb.controller.User;

import com.veloProWeb.model.entity.Record;
import com.veloProWeb.service.Record.IRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registro")
@CrossOrigin(origins = "http://localhost:4200")
public class RecordController {

    @Autowired private IRecordService recordService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('MASTER')")
    public List<Record> getAllRecord(){
        return recordService.getAllRecord();
    }
}
