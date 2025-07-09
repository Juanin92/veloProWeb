package com.veloproweb.controller.customer;

import com.veloproweb.model.dto.customer.PaymentRequestDTO;
import com.veloproweb.model.dto.customer.PaymentResponseDTO;
import com.veloproweb.service.customer.interfaces.IPaymentCustomerService;
import com.veloproweb.service.reporting.interfaces.IRecordService;
import com.veloproweb.util.ResponseMessage;
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
@RequestMapping("/pagos")
@AllArgsConstructor
@Validated
public class PaymentCustomerController {

    private final IPaymentCustomerService paymentCustomerService;
    private final IRecordService recordService;

    /**
     * Obtiene una lista de todos los pagos realizados.
     * @return - ResponseEntity con una lista de los pagos realizados
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER', 'GUEST')")
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayment(){
        return ResponseEntity.ok(paymentCustomerService.getAll());
    }

    /**
     * Obtiene una lista de pagos de un cliente específico por su ID.
     * @param customerId - Identificador del cliente cuyo pagos desean obtener
     * @return - ResponseEntity con una lista de los pagos del cliente
     */
    @GetMapping("/abonos")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER', 'GUEST')")
    public ResponseEntity<List<PaymentResponseDTO>> getCustomerSelectedPayment(@RequestParam @NotNull(
            message = "El ID no puede estar vacío") Long customerId){
        return ResponseEntity.ok(paymentCustomerService.getCustomerSelected(customerId));
    }

    /**
     * Crear pagos a la deuda de clientes
     * @param dto - Objeto con los valores necesarios para los pagos
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER', 'GUEST')")
    public ResponseEntity<Map<String, String>> createPaymentCustomer(@RequestBody @Valid PaymentRequestDTO dto,
                                                                     @AuthenticationPrincipal UserDetails userDetails){
        paymentCustomerService.createPaymentProcess(dto);
        recordService.registerAction(userDetails, "PAYMENT",
                "Abono cuenta cliente ID: " + dto.getCustomerID() + ", cantidad $" + dto.getAmount()
                        + " - " + dto.getComment());
        return new ResponseEntity<>(ResponseMessage.message("Pago registrado"), HttpStatus.CREATED);
    }
}
