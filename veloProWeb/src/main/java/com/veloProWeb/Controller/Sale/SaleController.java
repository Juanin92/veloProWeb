package com.veloProWeb.Controller.Sale;

import com.veloProWeb.Model.DTO.SaleRequestDTO;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Service.Sale.Interface.ISaleDetailService;
import com.veloProWeb.Service.Sale.Interface.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Controlador REST para gestionar operaciones relacionadas con ventas.
* Este controlador proporciona endpoints para agregar venta, obtener cantidad total de ventas y todas las ventas.
*/
@RestController
@RequestMapping("/ventas")
@CrossOrigin(origins = "http://localhost:4200")
public class SaleController {

    @Autowired private ISaleService saleService;
    @Autowired private ISaleDetailService saleDetailService;

    /**
     * Crear una venta y su detalle de venta correspondiente
     * @param dto - Objeto con los datos necesarios
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping()
    public ResponseEntity<Map<String, String>> createSale(@RequestBody SaleRequestDTO dto){
        Map<String, String> response = new HashMap<>();
        try {
            Sale sale = saleService.createSale(dto);
            saleDetailService.createSaleDetails(dto.getDetailList(), sale);
            response.put("message", "Venta registrada correctamente!");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Obtener la cantidad de ventas registrada
     * @return - Long con el valor resultante
     */
    @GetMapping()
    public ResponseEntity<Long> getTotalSale(){
        try{
            return ResponseEntity.ok(saleService.totalSales());
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     *  Obtener todas las ventas registradas
     * @return - Lista con las ventas registradas
     */
    @GetMapping("/lista-venta")
    public List<SaleRequestDTO> getAllSales(){
        return saleService.getAllSale();
    }
}
