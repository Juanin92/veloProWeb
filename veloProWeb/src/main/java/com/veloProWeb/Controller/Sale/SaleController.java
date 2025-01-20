package com.veloProWeb.Controller.Sale;

import com.veloProWeb.Service.SaleService.Interface.ISaleDetailService;
import com.veloProWeb.Service.SaleService.Interface.ISaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ventas")
@CrossOrigin(origins = "http://localhost:4200")
public class SaleController {

    @Autowired private ISaleService saleService;
    @Autowired private ISaleDetailService saleDetailService;

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
}
