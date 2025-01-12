package com.veloProWeb.Controller.Report;

import com.veloProWeb.Model.DTO.Report.DailySaleAvgDTO;
import com.veloProWeb.Model.DTO.Report.DailySaleCountDTO;
import com.veloProWeb.Model.DTO.Report.DailySaleEarningDTO;
import com.veloProWeb.Model.DTO.Report.DailySaleSumDTO;
import com.veloProWeb.Service.Report.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportController {

    @Autowired private IReportService reportService;

    @GetMapping("/cantidad_ventas")
    public ResponseEntity<Object> getDailySale(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        try{
            List<DailySaleCountDTO> dtoList = reportService.getDailySale(startDate, endDate);
            return ResponseEntity.ok(dtoList);
        }catch (IllegalArgumentException e){
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/monto_ventas")
    public ResponseEntity<Object> getTotalSaleDaily(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        try{
            List<DailySaleSumDTO> dtoList = reportService.getTotalSaleDaily(startDate, endDate);
            return ResponseEntity.ok(dtoList);
        }catch (IllegalArgumentException e){
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/promedio_ventas")
    public ResponseEntity<Object> getAverageTotalSaleDaily(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        try{
            List<DailySaleAvgDTO> dtoList = reportService.getAverageTotalSaleDaily(startDate, endDate);
            return ResponseEntity.ok(dtoList);
        }catch (IllegalArgumentException e){
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/ganancias_ventas")
    public ResponseEntity<Object> getEarningSale(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        try{
            List<DailySaleEarningDTO> dtoList = reportService.getEarningSale(startDate, endDate);
            return ResponseEntity.ok(dtoList);
        }catch (IllegalArgumentException e){
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
