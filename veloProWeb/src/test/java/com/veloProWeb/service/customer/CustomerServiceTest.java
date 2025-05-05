package com.veloProWeb.service.customer;

import com.veloProWeb.exceptions.Customer.CustomerAlreadyActivatedException;
import com.veloProWeb.exceptions.Customer.CustomerAlreadyDeletedException;
import com.veloProWeb.exceptions.Customer.CustomerAlreadyExistsException;
import com.veloProWeb.exceptions.Customer.CustomerNotFoundException;
import com.veloProWeb.exceptions.Validation.ValidationException;
import com.veloProWeb.mapper.CustomerMapper;
import com.veloProWeb.model.dto.customer.CustomerDTO;
import com.veloProWeb.model.dto.customer.CustomerResponseDTO;
import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.model.Enum.PaymentStatus;
import com.veloProWeb.repository.customer.CustomerRepo;
import com.veloProWeb.util.HelperService;
import com.veloProWeb.validation.CustomerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @InjectMocks private CustomerService customerService;
    @Mock private CustomerRepo customerRepo;
    @Spy private CustomerValidator validator;
    @Mock private HelperService helperService;
    @Spy CustomerMapper mapper = new CustomerMapper(new HelperService());
    private Customer customer;
    private CustomerDTO customerDTO;
    private CustomerResponseDTO customerResponseDTO;

    @BeforeEach
    void setUp(){
        customer = new Customer(1L,"Juan", "Perez Gonzalez", "+569 12345678", "test@test.com",
                0, 0, PaymentStatus.NULO, true, new ArrayList<>(), new ArrayList<>());
        customerDTO = CustomerDTO.builder()
                .id(1L)
                .name("Juan").surname("Perez Gonzalez")
                .phone("+569 12345600")
                .email(null).build();
        customerResponseDTO = CustomerResponseDTO.builder()
                .id(1L)
                .name("Juan").surname("Perez Gonzalez")
                .phone("+569 12345678").email("test@test.com")
                .debt(0).totalDebt(0)
                .status(PaymentStatus.PAGADA).account(true).build();
    }

    //Pruebas de creación de clientes nuevos
    @Test
    public void addNewCustomer_valid(){
        customerDTO.setName("jose");
        customerDTO.setSurname("perez");
        customer.setName("Jose");
        customer.setSurname("Perez Gonzalez");
        customer.setEmail(null);
        when(customerRepo.findBySimilarNameAndSurname(helperService.capitalize(customerDTO.getName()),
                helperService.capitalize(customerDTO.getSurname()))).thenReturn(Optional.empty());
        when(mapper.toEntity(customerDTO)).thenReturn(customer);

        customerService.addNewCustomer(customerDTO);
        verify(validator, times(1)).existCustomer(null);
        verify(validator, times(1)).validateInfoCustomer(customer);
        verify(customerRepo, times(1)).save(customer);

        assertEquals("Jose", customer.getName());
        assertEquals("Perez Gonzalez", customer.getSurname());
        assertEquals("x@x.xxx", customer.getEmail());
        assertNull(customer.getId());
        assertTrue(customer.isAccount());
        assertEquals(PaymentStatus.NULO, customer.getStatus());
    }
    @Test
    public void addNewCustomer_existingCustomer(){
        when(customerRepo.findBySimilarNameAndSurname(helperService.capitalize(customerDTO.getName()),
                helperService.capitalize(customerDTO.getSurname()))).thenReturn(Optional.of(customer));
        doThrow(new CustomerAlreadyExistsException("Cliente Existente: Hay registro de este cliente."))
                .when(validator).existCustomer(customer);
        CustomerAlreadyExistsException exception = assertThrows(CustomerAlreadyExistsException.class,
                () -> customerService.addNewCustomer(customerDTO));
        assertEquals("Cliente Existente: Hay registro de este cliente.", exception.getMessage());
        verify(validator, times(1)).existCustomer(customer);
        verify(validator, never()).validateInfoCustomer(customer);
        verify(mapper, never()).toEntity(customerDTO);
        verify(customerRepo, never()).save(customer);
    }

    //Pruebas de actualización de clientes
    @Test
    public void updateCustomer_valid(){
        customer.setStatus(PaymentStatus.PAGADA);
        when(customerRepo.findById(customerDTO.getId())).thenReturn(Optional.of(customer));

        customerService.updateCustomer(customerDTO);
        verify(customerRepo, times(1)).findById(customerDTO.getId());
        verify(mapper, times(1)).updateCustomerFromDto(customerDTO, customer);
        verify(validator, times(1)).validateInfoCustomer(customer);
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepo, times(1)).save(customerCaptor.capture());
        Customer capturedCustomer = customerCaptor.getValue();

        assertEquals(capturedCustomer.getName(), customerDTO.getName());
        assertEquals(capturedCustomer.getSurname(), customerDTO.getSurname());
        assertEquals("+569 12345600", capturedCustomer.getPhone());
        assertEquals("x@x.xxx", capturedCustomer.getEmail());
        assertEquals(PaymentStatus.PAGADA, capturedCustomer.getStatus());
        assertTrue(capturedCustomer.isAccount());
    }
    @Test
    public void updateCustomer_ExistingCustomer(){
        customerDTO.setId(2L);
        when(customerRepo.findById(customerDTO.getId())).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.updateCustomer(customerDTO));
        assertEquals("Cliente no encontrado", exception.getMessage());
        verify(customerRepo, times(1)).findById(customerDTO.getId());
        verify(validator, never()).validateInfoCustomer(customer);
        verify(mapper, never()).updateCustomerFromDto(customerDTO, customer);
        verify(customerRepo, never()).save(customer);
    }

    //Prueba para obtener todos los clientes de la BD
    @Test
    public void getAll_valid(){
        List<Customer> customers = List.of(customer);
        when(customerRepo.findAll()).thenReturn(List.of(customer));
        when(mapper.toResponseDTO(customer)).thenReturn(customerResponseDTO);
        List<CustomerResponseDTO> result = customerService.getAll();

        verify(customerRepo, times(1)).findAll();
        verify(mapper, times(1)).toResponseDTO(customer);
        assertEquals(result.size(), customers.size());
        assertEquals(result.getFirst().getName(), customers.getFirst().getName());
        assertEquals(result.getFirst().getSurname(), customers.getFirst().getSurname());
    }

    //Prueba para eliminar cliente seleccionado
    @Test
    public void delete_valid(){
        when(customerRepo.findById(customerDTO.getId())).thenReturn(Optional.of(customer));
        customerService.delete(customerDTO);

        verify(customerRepo, times(1)).findById(customerDTO.getId());
        verify(validator, times(1)).deleteCustomer(customer);
        verify(customerRepo, times(1)).save(customer);
        assertFalse(customer.isAccount());
    }
    @Test
    public void delete_NotActiveException(){
        customer.setAccount(false);
        when(customerRepo.findById(customerDTO.getId())).thenReturn(Optional.of(customer));
        doThrow(new CustomerAlreadyDeletedException("Cliente ya ha sido eliminado anteriormente.")).when(validator)
                .deleteCustomer(customer);
        CustomerAlreadyDeletedException e = assertThrows(CustomerAlreadyDeletedException.class,
                () -> customerService.delete(customerDTO));
        verify(customerRepo, times(1)).findById(customerDTO.getId());
        verify(validator, times(1)).deleteCustomer(customer);
        verify(customerRepo, never()).save(customer);
        assertEquals("Cliente ya ha sido eliminado anteriormente.", e.getMessage());
    }
    @Test
    public void delete_HasDebtException(){
        customer.setDebt(2000);
        when(customerRepo.findById(customerDTO.getId())).thenReturn(Optional.of(customer));
        doThrow(new ValidationException("El cliente tiene deuda pendiente, no se puede eliminar.")).when(validator)
                .deleteCustomer(customer);
        ValidationException e = assertThrows(ValidationException.class, () -> customerService.delete(customerDTO));
        verify(customerRepo, times(1)).findById(customerDTO.getId());
        verify(validator, times(1)).deleteCustomer(customer);
        verify(customerRepo, never()).save(customer);
        assertEquals("El cliente tiene deuda pendiente, no se puede eliminar.", e.getMessage());
    }

    //Prueba para activar cliente seleccionado
    @Test
    public void activate_valid(){
        customer.setAccount(false);
        when(customerRepo.findById(customerDTO.getId())).thenReturn(Optional.of(customer));
        customerService.activeCustomer(customerDTO);

        verify(customerRepo, times(1)).findById(customerDTO.getId());
        verify(validator, times(1)).isActive(customer);
        verify(customerRepo, times(1)).save(customer);
        assertTrue(customer.isAccount());
    }
    @Test
    public void activate_isActiveException(){
        when(customerRepo.findById(customerDTO.getId())).thenReturn(Optional.of(customer));
        doThrow(new CustomerAlreadyActivatedException("El cliente tiene su cuenta activada")).when(validator)
                .isActive(customer);
        CustomerAlreadyActivatedException e = assertThrows(CustomerAlreadyActivatedException.class,
                () -> customerService.activeCustomer(customerDTO));
        verify(customerRepo, times(1)).findById(customerDTO.getId());
        verify(validator, times(1)).isActive(customer);
        verify(customerRepo, never()).save(customer);
        assertEquals("El cliente tiene su cuenta activada", e.getMessage());
    }

    //Prueba validar el pago de la deuda
    @Test
    public void paymentDebt_valid(){
        customer.setDebt(2000);
        customerService.paymentDebt(customer, "1000");
        assertEquals(1000, customer.getDebt());
        verify(validator).validateValuePayment(1000, customer);
        verify(customerRepo, times(2)).save(customer);
    }

    //Pruebas para asignar el estado de los clientes por deudas vigentes
    @Test
    public void statusAssign_null(){
        customerService.statusAssign(customer);
        assertEquals(PaymentStatus.NULO, customer.getStatus());
        verify(customerRepo).save(customer);
    }
    @Test
    public void statusAssign_pending(){
        customer.setTotalDebt(2000);
        customer.setDebt(1500);
        customerService.statusAssign(customer);
        assertEquals(PaymentStatus.PENDIENTE, customer.getStatus());
        verify(customerRepo).save(customer);
    }
    @Test
    public void statusAssign_partial(){
        customer.setTotalDebt(2000);
        customer.setDebt(1000);
        customerService.statusAssign(customer);
        assertEquals(PaymentStatus.PARCIAL, customer.getStatus());
        verify(customerRepo).save(customer);
    }
    @Test
    public void statusAssign_paid(){
        customer.setTotalDebt(2000);
        customer.setDebt(0);
        customerService.statusAssign(customer);
        assertEquals(PaymentStatus.PAGADA, customer.getStatus());
        verify(customerRepo).save(customer);
    }

    //Prueba para agregar una venta al cliente
    @Test
    public void addSaleToCustomer_valid(){
        customer.setTotalDebt(20000);
        customerService.addSaleToCustomer(customer);
        assertEquals(20000, customer.getDebt());
        verify(customerRepo, times(2)).save(customer);
    }

    //Prueba para actualizar la deuda total del cliente
    @Test
    public void updateTotalDebt_valid(){
        customerService.updateTotalDebt(customer);
        verify(customerRepo).save(customer);
    }

    //Prueba para obtener un cliente por ID
    @Test
    public void getCustomerByID_valid(){
        when(customerRepo.findById(1L)).thenReturn(Optional.of(customer));
        Customer result = customerService.getCustomerById(1L);
        verify(customerRepo).findById(1L);
        assertEquals(1L, result.getId());
    }
    @Test
    public void getCustomerByID_invalid(){
        when(customerRepo.findById(1L)).thenReturn(Optional.empty());
        CustomerNotFoundException e = assertThrows(CustomerNotFoundException.class,() ->
                customerService.getCustomerById(1L));
        assertEquals("Cliente no encontrado", e.getMessage());
    }
}
