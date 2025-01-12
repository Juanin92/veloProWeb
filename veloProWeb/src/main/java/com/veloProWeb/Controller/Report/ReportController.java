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

/**
 * Controlador REST para gestionar operaciones relacionadas con reportes de ventas.
 * Este controlador proporciona endpoints para obtener listas.
 */
@RestController
@RequestMapping("/reportes")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportController {

    @Autowired private IReportService reportService;

    /**
     * Obtener el conteo diario de ventas en un rango de fechas.
     * @param startDate - Fecha de inicio del rango (inclusive).
     * @param endDate - Fecha de fin del rango (inclusive).
     * @return - ResponseEntity Lista de objetos DTO o mensaje de excepción
     */
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

    /**
     * Obtener la suma diaria de ventas en un rango de fechas.
     * @param startDate - Fecha de inicio del rango (inclusive).
     * @param endDate - Fecha de fin del rango (inclusive).
     * @return - ResponseEntity Lista de objetos DTO o mensaje de excepción
     */
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

    /**
     * Obtener el promedio diario de ventas en un rango de fechas.
     * @param startDate - Fecha de inicio del rango (inclusive).
     * @param endDate - Fecha de fin del rango (inclusive).
     * @return - ResponseEntity Lista de objetos DTO o mensaje de excepción
     */
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

    /**
     * Obtener las ganancias diarias de ventas en un rango de fechas.
     * @param startDate - Fecha de inicio del rango (inclusive).
     * @param endDate - Fecha de fin del rango (inclusive).
     * @return - ResponseEntity Lista de objetos DTO o mensaje de excepción
     */
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
