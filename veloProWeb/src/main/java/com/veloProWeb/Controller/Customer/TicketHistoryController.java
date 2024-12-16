package com.veloProWeb.Controller.Customer;

import com.veloProWeb.Model.DTO.TicketRequestDTO;
import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import com.veloProWeb.Service.Customer.Interfaces.ITicketHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Tickets")
@CrossOrigin(origins = "http://localhost:4200")
public class TicketHistoryController {

    @Autowired private ITicketHistoryService ticketHistoryService;

    @GetMapping
    public ResponseEntity<List<TicketHistory>> getListTicketByCustomer(@RequestParam Long customerId){
        return ResponseEntity.ok(ticketHistoryService.getByCustomerId(customerId));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createTicketToCustomer(@RequestBody TicketRequestDTO ticketDto){
        Map<String, String> response = new HashMap<>();
        try{
            ticketHistoryService.AddTicketToCustomer(ticketDto.getCustomer(), ticketDto.getNumber(), ticketDto.getTotal(), ticketDto.getDate());
            response.put("message","Ticket se ha creado para el cliente " + ticketDto.getCustomer().getName());
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("message", "Ocurrió un error inesperado. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/validar")
    public ResponseEntity<Map<String, String>> valideTicketByCustomer(@RequestBody Customer customer){
        Map<String, String> response = new HashMap<>();
        try{
            ticketHistoryService.valideTicketByCustomer(customer);
            response.put("message","Tickets validados para el cliente " + customer.getName());
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("message", "Ocurrió un error inesperado. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/actualizar-estado")
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
