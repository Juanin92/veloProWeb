package com.veloproweb.controller.customer;

import com.veloproweb.model.dto.customer.TicketResponseDTO;
import com.veloproweb.service.customer.interfaces.ITicketHistoryService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Tickets")
@Validated
@AllArgsConstructor
public class TicketHistoryController {

    private final ITicketHistoryService ticketHistoryService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER', 'GUEST')")
    public ResponseEntity<List<TicketResponseDTO>> getListTicketByCustomer(@RequestParam @NotNull(
            message = "El ID no puede estar vacío") Long customerId){
        return ResponseEntity.ok(ticketHistoryService.getByCustomerIdDTO(customerId));
    }
}
