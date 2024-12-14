package com.veloProWeb.Controller;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Service.Customer.Interfaces.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerController {

    @Autowired private ICustomerService customerService;

    @GetMapping
    public List<Customer> getListCustomer(){
        return customerService.getAll();
    }

    @PostMapping("/agregar")
    public ResponseEntity<Map<String, String>> addCustomer(@RequestBody Customer customer){
        Map<String, String> response = new HashMap<>();
        try {
            customerService.addNewCustomer(customer);
            response.put("message","Cliente agregado correctamente!");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<String> updateCustomer(@RequestBody Customer customer){
        try {
            customerService.updateCustomer(customer);
            return ResponseEntity.ok("{\"message\":\"Cliente actualizado correctamente!\"}");
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/eliminar")
    public ResponseEntity<String> deleteCustomer(@RequestBody Customer customer){
        try {
            customerService.delete(customer);
            return ResponseEntity.ok("{\"message\":\"Cliente eliminado correctamente!\"}");
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/activar")
    public ResponseEntity<Map<String, String>> activeCustomer(@RequestBody Customer customer){
        Map<String, String> response = new HashMap<>();
        try{
            customerService.activeCustomer(customer);
            response.put("message", "Cliente ha sido activado");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("message", "Ocurrió un error inesperado. " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
