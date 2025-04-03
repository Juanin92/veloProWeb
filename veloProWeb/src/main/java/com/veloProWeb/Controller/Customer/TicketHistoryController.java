package com.veloProWeb.Controller.Customer;

import com.veloProWeb.Model.DTO.TicketRequestDTO;
import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import com.veloProWeb.Service.Customer.Interfaces.ITicketHistoryService;
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
@RequestMapping("/Tickets")
@CrossOrigin(origins = "http://localhost:4200")
public class TicketHistoryController {

    @Autowired private ITicketHistoryService ticketHistoryService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene una lista de tickets de un cliente.
     * @param customerId - Identificador del cliente cuyos tickets desea obtener
     * @return - ResponseEntity con una lista de los tickets del cliente
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER', 'GUEST')")
    public ResponseEntity<List<TicketHistory>> getListTicketByCustomer(@RequestParam Long customerId){
        return ResponseEntity.ok(ticketHistoryService.getByCustomerId(customerId));
    }

    /**
     * Crea un ticket para un cliente
     * @param ticketDto - DTO que contiene los datos del ticket
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER')")
    public ResponseEntity<Map<String, String>> createTicketToCustomer(@RequestBody TicketRequestDTO ticketDto, @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            ticketHistoryService.AddTicketToCustomer(ticketDto.getCustomer(), ticketDto.getNumber(), ticketDto.getTotal(), ticketDto.getDate());
            response.put("message","Ticket se ha creado para el cliente " + ticketDto.getCustomer().getName());
            recordService.registerAction(userDetails, "TICKET", "Ticket creado para cliente "
                    + ticketDto.getCustomer().getName() + " " + ticketDto.getCustomer().getSurname());
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("message", "Ocurrió un error inesperado. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Actualiza el estado de un ticket
     * @param ticket - Ticket que se desea actualizar el estado.
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/actualizar-estado")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER', 'GUEST')")
    public ResponseEntity<Map<String, String>> updateStatus(@RequestBody TicketHistory ticket){
        Map<String, String> response = new HashMap<>();
        try{
            ticketHistoryService.updateStatus(ticket);
            response.put("message","Actualización del estado del ticket " + ticket.getDocument());
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("message", "Ocurrió un error inesperado. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
