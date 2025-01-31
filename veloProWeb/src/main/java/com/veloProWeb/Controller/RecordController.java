package com.veloProWeb.Controller;

import com.veloProWeb.Model.Entity.Record;
import com.veloProWeb.Service.Record.IRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registro")
@CrossOrigin(origins = "http://localhost:4200")
public class RecordController {

    @Autowired private IRecordService recordService;

    @GetMapping()
    public List<Record> getAllRecord(){
        return recordService.getAllRecord();
    }
}
