package com.veloProWeb.controller.sale;

import com.veloProWeb.model.Enum.DispatchStatus;
import com.veloProWeb.model.dto.DetailSaleRequestDTO;
import com.veloProWeb.model.dto.sale.DispatchRequestDTO;
import com.veloProWeb.model.dto.sale.DispatchResponseDTO;
import com.veloProWeb.model.entity.Sale.Dispatch;
import com.veloProWeb.service.reporting.interfaces.IRecordService;
import com.veloProWeb.service.sale.Interface.IDispatchService;
import com.veloProWeb.service.sale.Interface.ISaleDetailService;
import com.veloProWeb.util.ResponseMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/despachos")
@Validated
@AllArgsConstructor
public class DispatchController {

    private final IDispatchService dispatchService;
    private final ISaleDetailService saleDetailService;
    private final IRecordService recordService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'GUEST', 'SELLER')")
    public ResponseEntity<List<DispatchResponseDTO>> getDispatches(){
        return ResponseEntity.ok(dispatchService.getDispatches());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'GUEST', 'SELLER')")
    public ResponseEntity<Map<String, String>> createDispatch(@RequestBody @Valid DispatchRequestDTO dto,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        Dispatch dispatch = dispatchService.createDispatch(dto);
        saleDetailService.createSaleDetailsToDispatch(dto.getDetailSaleDTOS(), dispatch);
        recordService.registerAction(userDetails, "CREATE", "Despacho creado: " + dto.getCustomer());
        return new ResponseEntity<>(ResponseMessage.message("Despacho en preparación"), HttpStatus.CREATED);
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'SELLER')")
    public ResponseEntity<Map<String, String>> handleStatusDispatch(@RequestParam
                                                                    @NotNull(message = "El ID del despacho es obligatorio")
                                                                    Long dispatchID,
                                                                    @RequestParam
                                                                    @NotNull(message = "La acción aplicar del despacho es obligatorio")
                                                                    DispatchStatus action,
                                                                    @AuthenticationPrincipal UserDetails userDetails){
        dispatchService.handleStatus(dispatchID, action);
        recordService.registerAction(userDetails, "UPDATE", "Actualiza estado del despacho " + dispatchID);
        return ResponseEntity.ok(ResponseMessage.message("Cambio de estado del despacho"));
    }

    @GetMapping("/detalles")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'GUEST', 'SELLER')")
    public ResponseEntity<List<DetailSaleRequestDTO>> getDetailSale(@RequestParam
                                                                    @NotNull(message = "El ID del despacho es obligatorio")
                                                                    Long idDispatch){
        return ResponseEntity.ok(saleDetailService.getSaleDetailsToDispatch(idDispatch));
    }
}
