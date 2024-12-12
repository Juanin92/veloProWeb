package com.veloProWeb.Service;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Enum.PaymentStatus;
import com.veloProWeb.Repository.Customer.CustomerRepo;
import com.veloProWeb.Service.Customer.CustomerService;
import com.veloProWeb.Validation.CustomerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @InjectMocks private CustomerService customerService;
    @Mock private CustomerRepo customerRepo;
    @Mock private CustomerValidator validator;
    private Customer customer;

    @BeforeEach
    void setUp(){
        customer = new Customer(1L,"Juan", "Perez", "+569 12345678", "test@test.com", 0, 0, PaymentStatus.NULO, true, new ArrayList<>(), new ArrayList<>());
    }

    //Pruebas de creación de clientes nuevos
    @Test
    public void addNewCustomer_valid(){
        when(customerRepo.findBySimilarNameAndSurname(customer.getName(), customer.getSurname())).thenReturn(Optional.empty());
        customerService.addNewCustomer(customer);
        verify(validator).validate(customer);
        verify(customerRepo).save(customer);
    }
    @Test
    public void addNewCustomer_existingCustomer(){
        when(customerRepo.findBySimilarNameAndSurname(customer.getName(), customer.getSurname())).thenReturn(Optional.of(customer));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> customerService.addNewCustomer(customer));
        assertEquals("Cliente Existente: Hay registro de este cliente.", exception.getMessage());
        verify(validator, never()).validate(any(Customer.class));
        verify(customerRepo, never()).save(customer);
    }

    //Pruebas de actualización de clientes
    @Test
    public void updateCustomer_valid(){
        when(customerRepo.findBySimilarNameAndSurname(customer.getName(), customer.getSurname())).thenReturn(Optional.empty());
        customerService.updateCustomer(customer);
        verify(validator).validate(customer);
        verify(customerRepo).save(customer);
    }
    @Test
    public void updateCustomer_existingCustomer(){
        Customer customerDB = new Customer(2L,"Juan", "Perez", "+569 12345678", "test@test.com", 0, 0, PaymentStatus.NULO, true, new ArrayList<>(), new ArrayList<>());
        when(customerRepo.findBySimilarNameAndSurname(customerDB.getName(), customerDB.getSurname())).thenReturn(Optional.of(customerDB));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> customerService.addNewCustomer(customer));
        assertEquals("Cliente Existente: Hay registro de este cliente.", exception.getMessage());
        verify(validator, never()).validate(any(Customer.class));
    }

    //Prueba para obtener todos los clientes de la BD
    @Test
    public void getAll_valid(){
        customerService.getAll();
        verify(customerRepo).findAll();
    }

    //Prueba para eliminar cliente seleccionado
    @Test
    public void delete_valid(){
        customerService.delete(customer);
        verify(customerRepo).save(customer);
    }

    //Prueba para activar cliente seleccionado
    @Test
    public void active_valid(){
        customer.setAccount(false);
        customerService.activeCustomer(customer);
        verify(customerRepo).save(customer);
    }
    @Test
    public void active_invalid(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> customerService.activeCustomer(customer));
        assertEquals("El cliente tiene su cuenta activada", exception.getMessage());
    }
    @Test
    public void active_invalidNull(){
        customer.setId(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> customerService.activeCustomer(customer));
        assertEquals("Cliente no válido, Null", exception.getMessage());
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

    @Test
    public void addSaleToCustomer_valid(){
        customer.setTotalDebt(20000);
        customerService.addSaleToCustomer(customer);
        assertEquals(20000, customer.getDebt());
        verify(customerRepo, times(2)).save(customer);
    }

    @Test
    public void updateTotalDebt_valid(){
        customerService.updateTotalDebt(customer);
        verify(customerRepo).save(customer);
    }
}
