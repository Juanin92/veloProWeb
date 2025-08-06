package com.veloproweb.controller.sale;

import com.veloproweb.model.dto.sale.SaleRequestDTO;
import com.veloproweb.model.dto.sale.SaleResponseDTO;
import com.veloproweb.model.entity.sale.Sale;
import com.veloproweb.service.reporting.interfaces.IRecordService;
import com.veloproweb.service.sale.interfaces.IDispatchService;
import com.veloproweb.service.sale.interfaces.ISaleDetailService;
import com.veloproweb.service.sale.interfaces.ISaleService;
import com.veloproweb.util.EmailService;
import com.veloproweb.util.ResponseMessage;
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
@RequestMapping("/ventas")
@AllArgsConstructor
public class SaleController {

    private final ISaleService saleService;
    private final ISaleDetailService saleDetailService;
    private final IDispatchService dispatchService;
    private final IRecordService recordService;
    private final EmailService emailService;

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'GUEST', 'SELLER')")
    public ResponseEntity<Map<String, String>> createSale(@RequestBody SaleRequestDTO dto,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        Sale sale = saleService.createSale(dto);
        saleDetailService.addDetailsToSale(dto.getDetailList(), sale, userDetails);
        recordService.registerAction(userDetails, "CREATE", "Venta realizada " + sale.getDocument());
        return new ResponseEntity<>(ResponseMessage.message("Venta registrada correctamente!"), HttpStatus.CREATED);
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'GUEST', 'SELLER')")
    public ResponseEntity<Long> getTotalSale(){
        return ResponseEntity.ok(saleService.totalSales());
    }

    @GetMapping("/lista-venta")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'SELLER')")
    public ResponseEntity<List<SaleResponseDTO>> getAllSales(@AuthenticationPrincipal UserDetails userDetails){
        recordService.registerAction(userDetails, "VIEW", "Reporte - Lista de ventas realizadas");
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @PostMapping("venta_despacho")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'GUEST', 'SELLER')")
    public ResponseEntity<Map<String, String>> createSaleFromDispatch(@RequestBody SaleRequestDTO dto,
                                                                      @AuthenticationPrincipal UserDetails userDetails){
        dispatchService.handleDispatchReceiveToSale(dto.getIdDispatch());
        Sale sale = saleService.createSale(dto);
        saleDetailService.addSaleToSaleDetailsDispatch(dto.getIdDispatch(), sale);
        recordService.registerAction(userDetails, "CREATE","Venta realizada: por despacho");
        return new ResponseEntity<>(ResponseMessage.message("Venta registrada correctamente!"), HttpStatus.CREATED);
    }
}
