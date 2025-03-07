package com.veloProWeb.Controller.Customer;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Service.Customer.Interfaces.ICustomerService;
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

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasAnyRole;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerController {

    @Autowired private ICustomerService customerService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene una lista de clientes.
     * @return - ResponseEntity con una lista de clientes
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER')")
    public List<Customer> getListCustomer(){
        return customerService.getAll();
    }

    /**
     * Agrega un nuevo cliente
     * @param customer - Cliente con los datos que se desea agregar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
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

    /**
     * Actualiza los datos un cliente existente
     * @param customer - cliente con los datos actualizados
     * @param userDetails - Detalle de usuario autenticado.
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/actualizar")
    public ResponseEntity<String> updateCustomer(@RequestBody Customer customer, @AuthenticationPrincipal UserDetails userDetails){
        try {
            customerService.updateCustomer(customer);
            recordService.registerAction(userDetails, "UPDATE",
                    "Se actualizó el cliente: " + customer.getName() + " " + customer.getSurname());
            return ResponseEntity.ok("{\"message\":\"Cliente actualizado correctamente!\"}");
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     *Elimina un cliente
     * @param customer - Cliente que se desea eliminar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/eliminar")
    public ResponseEntity<String> deleteCustomer(@RequestBody Customer customer){
        try {
            customerService.delete(customer);
            return ResponseEntity.ok("{\"message\":\"Cliente eliminado correctamente!\"}");
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * Activa un cliente
     * @param customer - Cliente que se desea activar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
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
