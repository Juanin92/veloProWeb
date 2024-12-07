package com.veloProWeb.Service;

import com.veloProWeb.Model.Entity.Costumer.Costumer;
import com.veloProWeb.Model.Enum.PaymentStatus;
import com.veloProWeb.Repository.Costumer.CostumerRepo;
import com.veloProWeb.Service.Costumer.CostumerService;
import com.veloProWeb.Validation.CostumerValidator;
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
public class CostumerServiceTest {
    @InjectMocks private CostumerService costumerService;
    @Mock private CostumerRepo costumerRepo;
    @Mock private CostumerValidator validator;
    private Costumer costumer;

    @BeforeEach
    void setUp(){
        costumer = new Costumer(1L,"Juan", "Perez", "+569 12345678", "test@test.com", 0, 0, PaymentStatus.NULO, true, new ArrayList<>(), new ArrayList<>());
    }

    //Pruebas de creación de clientes nuevos
    @Test
    public void addNewCostumer_valid(){
        when(costumerRepo.findByNameAndSurname(costumer.getName(), costumer.getSurname())).thenReturn(Optional.empty());
        costumerService.addNewCostumer(costumer);
        verify(validator).validate(costumer);
        verify(costumerRepo).save(costumer);
    }
    @Test
    public void addNewCostumer_existingCostumer(){
        when(costumerRepo.findByNameAndSurname(costumer.getName(), costumer.getSurname())).thenReturn(Optional.of(costumer));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> costumerService.addNewCostumer(costumer));
        assertEquals("Cliente Existente: Hay registro de este cliente.", exception.getMessage());
        verify(validator, never()).validate(any(Costumer.class));
        verify(costumerRepo, never()).save(costumer);
    }

    //Pruebas de actualización de clientes
    @Test
    public void updateCostumer_valid(){
        when(costumerRepo.findByNameAndSurname(costumer.getName(), costumer.getSurname())).thenReturn(Optional.empty());
        costumerService.updateCostumer(costumer);
        verify(validator).validate(costumer);
        verify(costumerRepo).save(costumer);
    }
    @Test
    public void updateCostumer_existingCostumer(){
        Costumer costumerDB = new Costumer(2L,"Juan", "Perez", "+569 12345678", "test@test.com", 0, 0, PaymentStatus.NULO, true, new ArrayList<>(), new ArrayList<>());
        when(costumerRepo.findByNameAndSurname(costumerDB.getName(), costumerDB.getSurname())).thenReturn(Optional.of(costumerDB));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> costumerService.addNewCostumer(costumer));
        assertEquals("Cliente Existente: Hay registro de este cliente.", exception.getMessage());
        verify(validator, never()).validate(any(Costumer.class));
    }

    //Prueba para obtener todos los clientes de la BD
    @Test
    public void getAll_valid(){
        costumerService.getAll();
        verify(costumerRepo).findAll();
    }

    //Prueba para eliminar cliente seleccionado
    @Test
    public void delete_valid(){
        costumerService.delete(costumer);
        verify(costumerRepo).save(costumer);
    }

    //Prueba validar el pago de la deuda
    @Test
    public void paymentDebt_valid(){
        costumer.setDebt(2000);
        costumerService.paymentDebt(costumer, "1000");
        assertEquals(1000,costumer.getDebt());
        verify(validator).validateValuePayment(1000,costumer);
        verify(costumerRepo, times(2)).save(costumer);
    }

    //Pruebas para asignar el estado de los clientes por deudas vigentes
    @Test
    public void statusAssign_null(){
        costumerService.statusAssign(costumer);
        assertEquals(PaymentStatus.NULO, costumer.getStatus());
        verify(costumerRepo).save(costumer);
    }
    @Test
    public void statusAssign_pending(){
        costumer.setTotalDebt(2000);
        costumer.setDebt(1500);
        costumerService.statusAssign(costumer);
        assertEquals(PaymentStatus.PENDIENTE, costumer.getStatus());
        verify(costumerRepo).save(costumer);
    }
    @Test
    public void statusAssign_partial(){
        costumer.setTotalDebt(2000);
        costumer.setDebt(1000);
        costumerService.statusAssign(costumer);
        assertEquals(PaymentStatus.PARCIAL, costumer.getStatus());
        verify(costumerRepo).save(costumer);
    }
    @Test
    public void statusAssign_paid(){
        costumer.setTotalDebt(2000);
        costumer.setDebt(0);
        costumerService.statusAssign(costumer);
        assertEquals(PaymentStatus.PAGADA, costumer.getStatus());
        verify(costumerRepo).save(costumer);
    }

    @Test
    public void addSaleToCostumer_valid(){
        costumer.setTotalDebt(20000);
        costumerService.addSaleToCostumer(costumer);
        assertEquals(20000, costumer.getDebt());
        verify(costumerRepo, times(2)).save(costumer);
    }

    @Test
    public void updateTotalDebt_valid(){
        costumerService.updateTotalDebt(costumer);
        verify(costumerRepo).save(costumer);
    }
}
