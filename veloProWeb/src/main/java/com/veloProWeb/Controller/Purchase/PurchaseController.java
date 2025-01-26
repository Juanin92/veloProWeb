package com.veloProWeb.Controller.Purchase;

import com.veloProWeb.Model.DTO.PurchaseRequestDTO;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseDetailService;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar operaciones relacionadas con compras.
 * Este controlador proporciona endpoints para agregar y obtener cantidad total de compras.
 */
@RestController
@RequestMapping("/compras")
@CrossOrigin(origins = "http://localhost:4200")
public class PurchaseController {

    @Autowired private IPurchaseService purchaseService;
    @Autowired private IPurchaseDetailService purchaseDetailService;

    /**
     * Crear una compra y su detalle de compra correspondiente
     * @param dto - Objeto con los datos necesarios
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping("/crear")
    public ResponseEntity<Map<String, String>> createPurchase(@RequestBody PurchaseRequestDTO dto){
        Map<String, String> response = new HashMap<>();
        try {
            Purchase purchase = purchaseService.createPurchase(dto);
            purchaseDetailService.createDetailPurchase(dto.getDetailList(),purchase);
            response.put("message", "Compra registrada correctamente!");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Obtener la cantidad de compras registrada
     * @return - Long con el valor resultante
     */
    @GetMapping("/total_compras")
    public ResponseEntity<Long> getTotalPurchase(){
        try{
            return ResponseEntity.ok(purchaseService.totalPurchase());
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("lista-compras")
    public List<PurchaseRequestDTO> getAllPurchases(){
        return purchaseService.getAllPurchases();
    }
}
