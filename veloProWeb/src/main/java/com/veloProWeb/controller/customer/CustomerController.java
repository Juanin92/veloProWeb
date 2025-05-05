package com.veloProWeb.controller.customer;

import com.veloProWeb.model.dto.customer.CustomerDTO;
import com.veloProWeb.model.dto.customer.CustomerResponseDTO;
import com.veloProWeb.service.customer.interfaces.ICustomerService;
import com.veloProWeb.service.Record.IRecordService;
import com.veloProWeb.util.ResponseMessage;
import jakarta.validation.Valid;
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
@RequestMapping("/clientes")
@AllArgsConstructor
public class CustomerController {

    private final ICustomerService customerService;
    private final IRecordService recordService;

    /**
     * Obtiene una lista de clientes.
     * @return - ResponseEntity con una lista de clientes
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER', 'GUEST')")
    public List<CustomerResponseDTO> getListCustomer(){
        return customerService.getAll();
    }

    /**
     * Agrega un nuevo cliente
     * @param customer - Cliente con los datos que se desea agregar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping("/agregar")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER')")
    public ResponseEntity<Map<String, String>> addCustomer(@RequestBody @Valid CustomerDTO customer,
                                                           @AuthenticationPrincipal UserDetails userDetails){
        customerService.addNewCustomer(customer);
        recordService.registerAction(userDetails, "CREATE",
                "Se creó el cliente: " + customer.getName() + " " + customer.getSurname());
        return new ResponseEntity<>(ResponseMessage.message("Cliente agregado correctamente!"),HttpStatus.CREATED);
    }

    /**
     * Actualiza los datos un cliente existente
     * @param customer - cliente con los datos actualizados
     * @param userDetails - Detalle de usuario autenticado.
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/actualizar")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER')")
    public ResponseEntity<Map<String, String>> updateCustomer(@RequestBody @Valid CustomerDTO customer,
                                                 @AuthenticationPrincipal UserDetails userDetails){
        customerService.updateCustomer(customer);
        recordService.registerAction(userDetails, "UPDATE",
                "Se actualizó el cliente: " + customer.getName() + " " + customer.getSurname());
        return new ResponseEntity<>(ResponseMessage.message("Cliente actualizado correctamente!"), HttpStatus.OK);
    }

    /**
     *Elimina un cliente
     * @param customer - Cliente que se desea eliminar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/eliminar")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public ResponseEntity<Map<String, String>> deleteCustomer(@RequestBody CustomerDTO customer,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        customerService.delete(customer);
        recordService.registerAction(userDetails, "DELETE",
                "Se eliminó el cliente: " + customer.getName() + " " + customer.getSurname());
        return new ResponseEntity<>(ResponseMessage.message("Cliente eliminado correctamente!"), HttpStatus.OK);
    }

    /**
     * Activa un cliente
     * @param customer - Cliente que se desea activar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/activar")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SELLER', 'MASTER')")
    public ResponseEntity<Map<String, String>> activeCustomer(@RequestBody CustomerDTO customer,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        customerService.activeCustomer(customer);
        recordService.registerAction(userDetails, "ACTIVATE",
                "Se activó el cliente: " + customer.getName() + " " + customer.getSurname());
        return new ResponseEntity<>(ResponseMessage.message("Cliente ha sido activado"), HttpStatus.OK);
    }
}
