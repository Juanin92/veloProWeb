package com.veloProWeb.Controller.Sale;

import com.veloProWeb.Model.DTO.DetailSaleRequestDTO;
import com.veloProWeb.Model.DTO.SaleRequestDTO;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Entity.Sale.SaleDetail;
import com.veloProWeb.Service.Record.IRecordService;
import com.veloProWeb.Service.Sale.Interface.IDispatchService;
import com.veloProWeb.Service.Sale.Interface.ISaleDetailService;
import com.veloProWeb.Service.Sale.Interface.ISaleService;
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

@RestController
@RequestMapping("/ventas")
@CrossOrigin(origins = "http://localhost:4200")
public class SaleController {

    @Autowired private ISaleService saleService;
    @Autowired private ISaleDetailService saleDetailService;
    @Autowired private IDispatchService dispatchService;
    @Autowired private IRecordService recordService;

    /**
     * Crear una venta y su detalle de venta correspondiente
     * @param dto - Objeto con los datos necesarios
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'GUEST', 'SELLER')")
    public ResponseEntity<Map<String, String>> createSale(@RequestBody SaleRequestDTO dto,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try {
            Sale sale = saleService.createSale(dto);
            saleDetailService.createSaleDetailsToSale(dto.getDetailList(), sale);
            response.put("message", "Venta registrada correctamente!");
            recordService.registerAction(userDetails, "CREATE", "Venta realizada " + dto.getNumberDocument());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            recordService.registerAction(userDetails, "CREATE_FAILURE",
                    "Error: realizar venta " + dto.getNumberDocument() + " (" + e.getMessage() + ")");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Obtener la cantidad de ventas registrada
     * @return - Long con el valor resultante
     */
    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'GUEST', 'SELLER')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public List<SaleRequestDTO> getAllSales(@AuthenticationPrincipal UserDetails userDetails){
        recordService.registerAction(userDetails, "VIEW", "Reporte - Lista de ventas realizadas");
        return saleService.getAllSale();
    }

    /**
     * Obtener detalles de una venta especifica
     * @param idSale - Identificador de la venta seleccionada
     * @return - ResponseEntity con una lista de DTO con detalles de la venta
     */
    @GetMapping("/detalles")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'GUEST', 'SELLER')")
    public ResponseEntity<List<DetailSaleRequestDTO>> getDetailSale(@RequestParam Long idSale){
        try{
            return ResponseEntity.ok(saleDetailService.getSaleDetailsToSale(idSale));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Crea una venta a partir de un despacho existente.
     * @param dto - Objeto con los datos necesarios
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping("venta_despacho")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'GUEST', 'SELLER')")
    public ResponseEntity<Map<String, String>> createSaleFromDispatch(@RequestBody SaleRequestDTO dto,
                                                                      @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try {
            Long idDispatch = dto.getId();
            dispatchService.handleDispatchReceiveToSale(idDispatch);
            dto.setNumberDocument(saleService.totalSales().intValue());
            Sale sale = saleService.createSale(dto);
            saleDetailService.addSaleToSaleDetailsDispatch(idDispatch, sale);
            response.put("message", "Venta registrada correctamente!");
            recordService.registerAction(userDetails, "CREATE",
                    "Venta realizada: " + dto.getNumberDocument() + " por despacho");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            recordService.registerAction(userDetails, "CREATE_FAILURE",
                    "Error: realizar venta por despacho: " + dto.getNumberDocument() + " (" + e.getMessage() + ")");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
