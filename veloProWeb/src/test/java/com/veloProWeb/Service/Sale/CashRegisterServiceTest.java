package com.veloProWeb.Service.Sale;

import com.veloProWeb.Model.DTO.CashRegisterDTO;
import com.veloProWeb.Model.Entity.Sale.CashRegister;
import com.veloProWeb.Model.Entity.User.User;
import com.veloProWeb.Repository.Sale.CashRegisterRepo;
import com.veloProWeb.Service.User.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CashRegisterServiceTest {
    @InjectMocks private CashRegisterService cashRegisterService;
    @Mock private CashRegisterRepo cashRegisterRepo;
    @Mock private UserService userService;
    private CashRegister cashRegister;
    private User user;
    private CashRegisterDTO dto;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setId(1L);
        user.setName("Juan");
        user.setSurname("Perez");
        user.setUsername("jpp");

        dto = new CashRegisterDTO(1L, LocalDateTime.now(), null, 5000,
                50000, 1000, null, null, "jpp");

        cashRegister = new CashRegister();
        cashRegister.setId(1L);
        cashRegister.setDateOpening(LocalDateTime.now());
        cashRegister.setDateClosing(LocalDateTime.now());
        cashRegister.setAmountOpening(5000);
        cashRegister.setAmountClosingCash(1000);
        cashRegister.setComment("Prueba");
        cashRegister.setStatus("Cerrado");
        cashRegister.setUser(user);
    }

    //Prueba para registrar una apertura de la caja
    @Test
    public void addRegisterOpening_valid(){
        when(userService.getUserWithUsername("jpp")).thenReturn(user);
        cashRegisterService.addRegisterOpening("jpp", 1000);

        ArgumentCaptor<CashRegister> captor = ArgumentCaptor.forClass(CashRegister.class);
        verify(userService, times(1)).getUserWithUsername("jpp");
        verify(cashRegisterRepo, times(1)).save(captor.capture());

        CashRegister savedRegister = captor.getValue();
        assertEquals("OPEN", savedRegister.getStatus());
        assertEquals(1000, savedRegister.getAmountOpening());
    }
    @Test
    public void addRegisterOpening_invalidAmount(){
        when(userService.getUserWithUsername("jpp")).thenReturn(user);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> cashRegisterService.addRegisterOpening("jpp", 0));

        verify(userService, times(1)).getUserWithUsername("jpp");
        verify(cashRegisterRepo, never()).save(any(CashRegister.class));

        assertEquals("El monto debe ser mayor a 0", e.getMessage());
    }

    //Prueba para crear un registro de cierre de caja
    @Test
    public void addRegisterClosing_valid(){
        cashRegister.setDateClosing(null);
        cashRegister.setAmountClosingCash(0);
        cashRegister.setAmountClosingPos(0);
        cashRegister.setStatus("OPEN");
        cashRegister.setComment(null);

        when(userService.getUserWithUsername("jpp")).thenReturn(user);
        when(cashRegisterRepo.findLatestOpenRegisterByUser(user.getId())).thenReturn(cashRegister);
        cashRegisterService.addRegisterClosing(dto);

        verify(userService, times(1)).getUserWithUsername("jpp");
        verify(cashRegisterRepo, times(1)).findLatestOpenRegisterByUser(user.getId());
        verify(cashRegisterRepo, times(1)).save(cashRegister);

        assertEquals(50000, cashRegister.getAmountClosingCash());
        assertEquals(1000, cashRegister.getAmountClosingPos());
        assertEquals("CLOSED", cashRegister.getStatus());
    }
    @Test
    public void addRegisterClosing_invalidClosingAndOpeningAmount(){
        cashRegister.setDateClosing(null);
        cashRegister.setAmountOpening(100000);
        cashRegister.setAmountClosingCash(0);
        cashRegister.setAmountClosingPos(0);
        cashRegister.setStatus("OPEN");
        cashRegister.setComment(null);

        when(userService.getUserWithUsername("jpp")).thenReturn(user);
        when(cashRegisterRepo.findLatestOpenRegisterByUser(user.getId())).thenReturn(cashRegister);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> cashRegisterService.addRegisterClosing(dto));

        verify(userService, times(1)).getUserWithUsername("jpp");
        verify(cashRegisterRepo, times(1)).findLatestOpenRegisterByUser(user.getId());
        verify(cashRegisterRepo, never()).save(cashRegister);

        assertEquals("El monto de cierre en efectivo es menor a la apertura.", e.getMessage());
    }

    //Prueba para crear comentario a un registro existente
    @Test
    public void addRegisterValidateComment_valid(){
        dto.setComment("Prueba Test");
        cashRegister.setComment(null);

        when(cashRegisterRepo.findById(dto.getId())).thenReturn(Optional.of(cashRegister));
        cashRegisterService.addRegisterValidateComment(dto);

        verify(cashRegisterRepo, times(1)).findById(dto.getId());
        verify(cashRegisterRepo, times(1)).save(cashRegister);

        assertEquals(dto.getComment(), cashRegister.getComment());
    }
    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    public void addRegisterValidateComment_invalidEmptyComment(String comment){
        dto.setComment(comment);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> cashRegisterService.addRegisterValidateComment(dto));

        verify(cashRegisterRepo, never()).findById(dto.getId());
        verify(cashRegisterRepo, never()).save(cashRegister);

        assertEquals("Debes agregar un comentario para registrar.", e.getMessage());
    }

    //Prueba para obtener un registro de caja por su identificador
    @Test
    public void getRegisterByUser_valid(){
        when(cashRegisterRepo.findLatestOpenRegisterByUser(1L)).thenReturn(cashRegister);
        CashRegister result = cashRegisterService.getRegisterByUser(1L);

        verify(cashRegisterRepo, times(1)).findLatestOpenRegisterByUser(1L);
        assertEquals(result, cashRegister);
    }
    @Test
    public void getRegisterByUser_invalidNullValue(){
        when(cashRegisterRepo.findLatestOpenRegisterByUser(4L)).thenReturn(null);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> cashRegisterService.getRegisterByUser(4L));

        verify(cashRegisterRepo, times(1)).findLatestOpenRegisterByUser(4L);
        assertEquals("No hay registro de apertura válido.", e.getMessage());
    }
    @Test
    public void getRegisterByUser_invalidDateNotMatch(){
        cashRegister.setDateOpening(LocalDateTime.now().plusDays(2));
        when(cashRegisterRepo.findLatestOpenRegisterByUser(1L)).thenReturn(cashRegister);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> cashRegisterService.getRegisterByUser(1L));

        verify(cashRegisterRepo, times(1)).findLatestOpenRegisterByUser(1L);
        assertEquals("La fecha no coincide con la apertura.", e.getMessage());
    }

    //Prueba para obtener lista de cashRegister
    @Test
    public void getAll_valid(){
        List<CashRegister> list = Collections.singletonList(cashRegister);
        when(cashRegisterRepo.findAll()).thenReturn(list);
        List<CashRegisterDTO> result = cashRegisterService.getAll();
        verify(cashRegisterRepo).findAll();
        assertEquals(list.size(), result.size());
        assertEquals("Juan Perez", result.getFirst().getUser());
    }

    //Prueba para actualizar los montos del registro existente
    @Test
    public void updateRegister_valid(){
        when(cashRegisterRepo.findById(1L)).thenReturn(Optional.of(cashRegister));
        cashRegisterService.updateRegister(dto);

        verify(cashRegisterRepo, times(1)).findById(1L);
        verify(cashRegisterRepo, times(1)).save(cashRegister);

        assertEquals(dto.getAmountOpening(), cashRegister.getAmountOpening());
        assertEquals(dto.getAmountClosingPos(), cashRegister.getAmountClosingPos());
        assertEquals(dto.getAmountClosingCash(), cashRegister.getAmountClosingCash());
    }
}
