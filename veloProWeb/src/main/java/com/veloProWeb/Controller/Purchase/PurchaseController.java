package com.veloProWeb.Controller.Purchase;

import com.veloProWeb.Model.DTO.PurchaseRequestDTO;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseDetailService;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/compras")
@CrossOrigin(origins = "http://localhost:4200")
public class PurchaseController {

    @Autowired private IPurchaseService purchaseService;
    @Autowired private IPurchaseDetailService purchaseDetailService;

    @PostMapping("/crear")
    public ResponseEntity<Purchase> createPurchase(@RequestBody PurchaseRequestDTO dto){
        try {
            Purchase purchase = purchaseService.createPurchase(dto.getDate(),dto.getSupplier(), dto.getDocumentType(), dto.getDocument(), dto.getTax(), dto.getTotal());
            return ResponseEntity.ok(purchase);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/total_compras")
    public ResponseEntity<Long> getTotalPurchase(){
        try{
            return ResponseEntity.ok(purchaseService.totalPurchase());
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

//    @GetMapping("/total_Precio_Compras")
//    public ResponseEntity<Long> getTotalPricePurchase(){
//        try{
//            return ResponseEntity.ok(purchaseService.totalPricePurchase());
//        }catch (IllegalArgumentException e){
//            return ResponseEntity.badRequest().build();
//        }
//    }
}
