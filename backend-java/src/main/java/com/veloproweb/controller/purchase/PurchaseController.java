package com.veloproweb.controller.purchase;

import com.veloproweb.model.dto.purchase.PurchaseRequestDTO;
import com.veloproweb.model.dto.purchase.PurchaseResponseDTO;
import com.veloproweb.model.entity.purchase.Purchase;
import com.veloproweb.service.purchase.interfaces.IPurchaseDetailService;
import com.veloproweb.service.purchase.interfaces.IPurchaseService;
import com.veloproweb.service.reporting.interfaces.IRecordService;
import com.veloproweb.util.ResponseMessage;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/compras")
@AllArgsConstructor
public class PurchaseController {

    private final IPurchaseService purchaseService;
    private final IPurchaseDetailService purchaseDetailService;
    private final IRecordService recordService;

    @PostMapping("/crear")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> createPurchase(@RequestBody @Valid PurchaseRequestDTO dto,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        Purchase purchase = purchaseService.createPurchase(dto);
        purchaseDetailService.createPurchaseDetail(userDetails, dto.getDetailList(),purchase);
        recordService.registerAction(userDetails, "CREATE", "Compra creada: " + dto.getDocument());
        return new ResponseEntity<>(ResponseMessage.message("Compra registrada correctamente!"),
                HttpStatus.CREATED);
    }

    @GetMapping("/total_compras")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<Long> getTotalPurchase(){
        return ResponseEntity.ok(purchaseService.totalPurchase());
    }

    @GetMapping("/lista-compras")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<PurchaseResponseDTO>> getAllPurchases(@AuthenticationPrincipal UserDetails userDetails){
        recordService.registerAction(userDetails, "VIEW_PURCHASE",
                "Observo reporte de compra");
        return ResponseEntity.ok(purchaseService.getAllPurchases());
    }
}
