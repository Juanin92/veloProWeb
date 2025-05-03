package com.veloProWeb.Controller.Customer;

import com.veloProWeb.Model.DTO.PaymentRequestDTO;
import com.veloProWeb.Model.Entity.Customer.PaymentCustomer;
import com.veloProWeb.Service.Customer.Interfaces.IPaymentCustomerService;
import com.veloProWeb.Service.Record.IRecordService;
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

@RestController
@RequestMapping("/pagos")
public class PaymentCustomerController {

    @Autowired private IPaymentCustomerService paymentCustomerService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene una lista de todos los pagos realizados.
     * @return - ResponseEntity con una lista de los pagos realizados
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER', 'GUEST')")
    public ResponseEntity<List<PaymentCustomer>> getAllPayment(){
        return ResponseEntity.ok(paymentCustomerService.getAll());
    }

    /**
     * Obtiene una lista de pagos de un cliente específico por su ID.
     * @param customerId - Identificador del cliente cuyo pagos desean obtener
     * @return - ResponseEntity con una lista de los pagos del cliente
     */
    @GetMapping("/abonos")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER', 'GUEST')")
    public ResponseEntity<List<PaymentCustomer>> getCustomerSelectedPayment(@RequestParam Long customerId){
        return ResponseEntity.ok(paymentCustomerService.getCustomerSelected(customerId));
    }

    /**
     * Crear pagos a la deuda de clientes
     * @param dto - Objeto con los valores necesarios para los pagos
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER', 'GUEST')")
    public ResponseEntity<Map<String, String>> createPaymentCustomer(@RequestBody PaymentRequestDTO dto, @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            response.put("message", "Pago registrado");
            paymentCustomerService.createPaymentProcess(dto);
            recordService.registerAction(userDetails, "PAYMENT",
                    "Abono cuenta cliente ID: " + dto.getCustomerID() + ", cantidad $" + dto.getAmount()
                            + " - " + dto.getComment());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
