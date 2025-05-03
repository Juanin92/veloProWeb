package com.veloProWeb.controller.Purchase;

import com.veloProWeb.model.dto.DetailPurchaseRequestDTO;
import com.veloProWeb.model.dto.PurchaseRequestDTO;
import com.veloProWeb.model.entity.Purchase.Purchase;
import com.veloProWeb.service.Purchase.Interfaces.IPurchaseDetailService;
import com.veloProWeb.service.Purchase.Interfaces.IPurchaseService;
import com.veloProWeb.service.Record.IRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    @Autowired private IRecordService recordService;

    /**
     * Crear una compra y su detalle de compra correspondiente
     * @param dto - Objeto con los datos necesarios
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping("/crear")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> createPurchase(@RequestBody PurchaseRequestDTO dto,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try {
            Purchase purchase = purchaseService.createPurchase(dto);
            purchaseDetailService.createDetailPurchase(dto.getDetailList(),purchase);
            recordService.registerAction(userDetails, "CREATE", "Compra creada: " + dto.getDocument());
            response.put("message", "Compra registrada correctamente!");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            recordService.registerAction(userDetails, "CREATE_FAILURE",
                    "Error: crear compra: " + dto.getDocument());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Obtener la cantidad de compras registrada
     * @return - Long con el valor resultante
     */
    @GetMapping("/total_compras")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Long> getTotalPurchase(){
        try{
            return ResponseEntity.ok(purchaseService.totalPurchase());
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene todas las compras registradas
     * @return - Lista con las compras
     */
    @GetMapping("/lista-compras")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public List<PurchaseRequestDTO> getAllPurchases(@AuthenticationPrincipal UserDetails userDetails){
        recordService.registerAction(userDetails, "VIEW_PURCHASE",
                "Observo reporte de compra");
        return purchaseService.getAllPurchases();
    }

    /**
     * Obtiene una compra especifíca
     * @param idPurchase - Identificador de la compra a buscar
     * @return - ResponseEntity con la lista dto con detalle de la compra, si no NOT_FOUND no encuentra nada
     */
    @GetMapping("/detalles")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<DetailPurchaseRequestDTO>> getDetails(@RequestParam Long idPurchase){
        try{
            return ResponseEntity.ok(purchaseDetailService.getPurchaseDetails(idPurchase));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
