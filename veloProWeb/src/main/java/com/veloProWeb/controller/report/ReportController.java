package com.veloProWeb.controller.report;

import com.veloProWeb.model.dto.Report.*;
import com.veloProWeb.service.Record.IRecordService;
import com.veloProWeb.service.report.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    @Autowired private IRecordService recordService;

    /**
     * Obtener el conteo diario de ventas en un rango de fechas.
     * @param startDate - Fecha de inicio del rango (inclusive).
     * @param endDate - Fecha de fin del rango (inclusive).
     * @return - ResponseEntity Lista de objetos dto o mensaje de excepción
     */
    @GetMapping("/cantidad_ventas")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Object> getDailySale(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate,
                                               @AuthenticationPrincipal UserDetails userDetails){
        try{
            List<DailySaleCountDTO> dtoList = reportService.getDailySale(startDate, endDate);
            recordService.registerAction(userDetails, "VIEW_CHART", "Gráfico de ventas diarias");
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
     * @return - ResponseEntity Lista de objetos dto o mensaje de excepción
     */
    @GetMapping("/monto_ventas")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Object> getTotalSaleDaily(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate,
                                                    @AuthenticationPrincipal UserDetails userDetails){
        try{
            List<DailySaleSumDTO> dtoList = reportService.getTotalSaleDaily(startDate, endDate);
            recordService.registerAction(userDetails, "VIEW_CHART", "Gráfico de suma de ventas diarias");
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
     * @return - ResponseEntity Lista de objetos dto o mensaje de excepción
     */
    @GetMapping("/promedio_ventas")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Object> getAverageTotalSaleDaily(@RequestParam LocalDate startDate,
                                                           @RequestParam LocalDate endDate,
                                                           @AuthenticationPrincipal UserDetails userDetails){
        try{
            List<DailySaleAvgDTO> dtoList = reportService.getAverageTotalSaleDaily(startDate, endDate);
            recordService.registerAction(userDetails, "VIEW_CHART", "Gráfico de promedio ventas diarias");
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
     * @return - ResponseEntity Lista de objetos dto o mensaje de excepción
     */
    @GetMapping("/ganancias_ventas")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Object> getEarningSale(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate,
                                                 @AuthenticationPrincipal UserDetails userDetails){
        try{
            List<DailySaleEarningDTO> dtoList = reportService.getEarningSale(startDate, endDate);
            recordService.registerAction(userDetails, "VIEW_CHART", "Gráfico de ganancias ventas diarias");
            return ResponseEntity.ok(dtoList);
        }catch (IllegalArgumentException e){
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Obtener los productos más vendidos en un rango de fechas.
     * @param startDate - Fecha de inicio del rango (inclusive).
     * @param endDate - Fecha de fin del rango (inclusive).
     * @return - ResponseEntity Lista de objetos dto o mensaje de excepción
     */
    @GetMapping("/producto_ventas")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Object> getMostProductSale(@RequestParam LocalDate startDate,
                                                     @RequestParam LocalDate endDate,
                                                     @AuthenticationPrincipal UserDetails userDetails){
        try{
            List<ProductReportDTO> dtoList = reportService.getMostProductSale(startDate, endDate);
            recordService.registerAction(userDetails, "VIEW_CHART", "Gráfico de productos más vendidos");
            return ResponseEntity.ok(dtoList);
        }catch (IllegalArgumentException e){
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Obtener las categorías más vendidas en un rango de fechas.
     * @param startDate - Fecha de inicio del rango (inclusive).
     * @param endDate - Fecha de fin del rango (inclusive).
     * @return - ResponseEntity Lista de objetos dto o mensaje de excepción
     */
    @GetMapping("/categoria_ventas")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Object> getMostCategorySale(@RequestParam LocalDate startDate,
                                                      @RequestParam LocalDate endDate,
                                                      @AuthenticationPrincipal UserDetails userDetails){
        try{
            List<ProductReportDTO> dtoList = reportService.getMostCategorySale(startDate, endDate);
            recordService.registerAction(userDetails, "VIEW_CHART", "Gráfico de categorías más vendidas");
            return ResponseEntity.ok(dtoList);
        }catch (IllegalArgumentException e){
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
