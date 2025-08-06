package com.veloproweb.service.sale;

import com.veloproweb.exceptions.sale.CashRegisterDateNotMatchException;
import com.veloproweb.exceptions.sale.CashRegisterNotFoundException;
import com.veloproweb.exceptions.sale.InvalidAmountCashRegisterException;
import com.veloproweb.exceptions.sale.UnauthorizedCashRegisterAccessException;
import com.veloproweb.mapper.CashRegisterMapper;
import com.veloproweb.model.enums.Rol;
import com.veloproweb.model.dto.sale.CashRegisterRequestDTO;
import com.veloproweb.model.dto.sale.CashRegisterResponseDTO;
import com.veloproweb.model.entity.sale.CashRegister;
import com.veloproweb.model.entity.user.User;
import com.veloproweb.repository.sale.CashRegisterRepo;
import com.veloproweb.repository.UserRepo;
import com.veloproweb.service.user.interfaces.IUserService;
import com.veloproweb.validation.CashRegisterValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CashRegisterServiceTest {
    @InjectMocks private CashRegisterService cashRegisterService;
    @Mock private CashRegisterRepo cashRegisterRepo;
    @Mock private UserRepo userRepo;
    @Mock private IUserService userService;
    @Mock private CashRegisterValidator validator;
    @Spy private CashRegisterMapper mapper;
    private User user;

    @BeforeEach
    void setUp(){
        user = User.builder().id(1L).name("John").surname("Doe").username("johnny").role(Rol.ADMIN).build();
    }

    //Prueba para registrar una apertura de la caja
    @Test
    void openRegister(){
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        doNothing().when(validator).validateRoleCanRegister(false);
        doNothing().when(validator).validateAmount(1000);
        CashRegister cashRegisterMapped = mapper.toOpeningRegisterEntity(user, 1000);
        when(mapper.toOpeningRegisterEntity(user, 1000)).thenReturn(cashRegisterMapped);

        ArgumentCaptor<CashRegister> cashRegisterCaptor = ArgumentCaptor.forClass(CashRegister.class);
        cashRegisterService.openRegister("johnny", 1000);

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(validator, times(1)).validateRoleCanRegister(false);
        verify(validator, times(1)).validateAmount(1000);
        verify(cashRegisterRepo, times(1)).save(cashRegisterCaptor.capture());

        CashRegister savedRegister = cashRegisterCaptor.getValue();
        assertNull(savedRegister.getDateClosing(), savedRegister.getComment());
        assertEquals("OPEN", savedRegister.getStatus());
        assertEquals(1000, savedRegister.getAmountOpening());
        assertEquals(0, savedRegister.getAmountClosingCash(), savedRegister.getAmountClosingPos());
        assertFalse(savedRegister.isAlert());
        assertEquals(user, savedRegister.getUser());
    }
    @Test
    void openRegister_roleException(){
        User wareHouseUser = User.builder().id(1L).name("John").surname("Doe").username("johnny")
                .role(Rol.WAREHOUSE).build();
        when(userService.getUserByUsername("johnny")).thenReturn(wareHouseUser);
        doThrow(new UnauthorizedCashRegisterAccessException("Este rol no puede operar con registros de caja"))
                .when(validator).validateRoleCanRegister(true);
        UnauthorizedCashRegisterAccessException e = assertThrows(UnauthorizedCashRegisterAccessException.class,
                () -> cashRegisterService.openRegister("johnny", 1000));

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(validator, times(1)).validateRoleCanRegister(true);
        verify(cashRegisterRepo, never()).save(any(CashRegister.class));

        assertEquals("Este rol no puede operar con registros de caja", e.getMessage());
    }
    @Test
    void openRegister_amountException(){
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        doNothing().when(validator).validateRoleCanRegister(false);
        doThrow(new InvalidAmountCashRegisterException("El monto de la caja debe ser mayor a 0"))
                .when(validator).validateAmount(0);
        InvalidAmountCashRegisterException e = assertThrows(InvalidAmountCashRegisterException.class,
                () -> cashRegisterService.openRegister("johnny", 0));

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(validator, times(1)).validateRoleCanRegister(false);
        verify(cashRegisterRepo, never()).save(any(CashRegister.class));

        assertEquals("El monto de la caja debe ser mayor a 0", e.getMessage());
    }

    //Prueba para crear un registro de cierre de caja
    @Test
    void closeRegister(){
        CashRegisterRequestDTO dto = CashRegisterRequestDTO.builder().amountClosingCash(1500).amountClosingPos(500)
                .comment("Test comment").build();
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        doNothing().when(validator).validateRoleCanRegister(false);
        CashRegister cashRegister = CashRegister.builder().user(user).build();
        when(cashRegisterRepo.findLatestOpenRegisterByUser(user.getId())).thenReturn(cashRegister);
        doNothing().when(validator).validateCloseRegister(dto, cashRegister);

        ArgumentCaptor<CashRegister> cashRegisterCaptor = ArgumentCaptor.forClass(CashRegister.class);
        cashRegisterService.closeRegister("johnny", dto);

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(validator, times(1)).validateRoleCanRegister(false);
        verify(cashRegisterRepo, times(1)).findLatestOpenRegisterByUser(user.getId());
        verify(cashRegisterRepo, times(1)).save(cashRegisterCaptor.capture());

        CashRegister savedRegister = cashRegisterCaptor.getValue();
        assertEquals(1500, savedRegister.getAmountClosingCash());
        assertEquals(500, savedRegister.getAmountClosingPos());
        assertEquals("CLOSED", savedRegister.getStatus());
        assertEquals("Test comment", savedRegister.getComment());
        assertFalse(savedRegister.isAlert());
    }
    @Test
    void closeRegister_alert(){
        CashRegisterRequestDTO dto = CashRegisterRequestDTO.builder().amountClosingCash(1500).amountClosingPos(500)
                .comment("error Test comment").build();
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        doNothing().when(validator).validateRoleCanRegister(false);
        CashRegister cashRegister = CashRegister.builder().user(user).build();
        when(cashRegisterRepo.findLatestOpenRegisterByUser(user.getId())).thenReturn(cashRegister);
        doNothing().when(validator).validateCloseRegister(dto, cashRegister);

        ArgumentCaptor<CashRegister> cashRegisterCaptor = ArgumentCaptor.forClass(CashRegister.class);
        cashRegisterService.closeRegister("johnny", dto);

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(validator, times(1)).validateRoleCanRegister(false);
        verify(cashRegisterRepo, times(1)).findLatestOpenRegisterByUser(user.getId());
        verify(cashRegisterRepo, times(1)).save(cashRegisterCaptor.capture());

        CashRegister savedRegister = cashRegisterCaptor.getValue();
        assertEquals(1500, savedRegister.getAmountClosingCash());
        assertEquals(500, savedRegister.getAmountClosingPos());
        assertEquals("CLOSED", savedRegister.getStatus());
        assertEquals("error Test comment", savedRegister.getComment());
        assertTrue(savedRegister.isAlert());
    }
    @Test
    void closeRegister_differentOpeningAndClosingAmount(){
        CashRegisterRequestDTO dto = CashRegisterRequestDTO.builder().amountClosingCash(1500).amountClosingPos(500)
                .comment("Test comment").build();
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        doNothing().when(validator).validateRoleCanRegister(false);
        CashRegister cashRegister = CashRegister.builder().user(user).amountOpening(2000).build();
        when(cashRegisterRepo.findLatestOpenRegisterByUser(user.getId())).thenReturn(cashRegister);
        doNothing().when(validator).validateCloseRegister(dto, cashRegister);

        ArgumentCaptor<CashRegister> cashRegisterCaptor = ArgumentCaptor.forClass(CashRegister.class);
        cashRegisterService.closeRegister("johnny", dto);

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(validator, times(1)).validateRoleCanRegister(false);
        verify(cashRegisterRepo, times(1)).findLatestOpenRegisterByUser(user.getId());
        verify(cashRegisterRepo, times(2)).save(cashRegisterCaptor.capture());

        CashRegister savedRegister = cashRegisterCaptor.getValue();
        assertEquals(1500, savedRegister.getAmountClosingCash());
        assertEquals(500, savedRegister.getAmountClosingPos());
        assertEquals("CLOSED", savedRegister.getStatus());
        assertEquals("Test comment - El monto de cierre en efectivo es menor a la apertura.",
                savedRegister.getComment());
        assertTrue(savedRegister.isAlert());
    }
    @Test
    void closeRegisterClosing_cashRegisterDoesNotExists(){
        CashRegisterRequestDTO dto = CashRegisterRequestDTO.builder().amountClosingCash(1500).amountClosingPos(500)
                .comment("Test comment").build();
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        doNothing().when(validator).validateRoleCanRegister(false);
        when(cashRegisterRepo.findLatestOpenRegisterByUser(user.getId())).thenReturn(null);
        doThrow(new CashRegisterNotFoundException("No hay registro de apertura válido.")).when(validator)
                .validateCloseRegister(dto, null);

        CashRegisterNotFoundException e = assertThrows(CashRegisterNotFoundException.class,
                () -> cashRegisterService.closeRegister("johnny", dto));

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(validator, times(1)).validateRoleCanRegister(false);
        verify(cashRegisterRepo, times(1)).findLatestOpenRegisterByUser(user.getId());
        verify(cashRegisterRepo, never()).save(any(CashRegister.class));

        assertEquals("No hay registro de apertura válido.", e.getMessage());
    }
    @Test
    void closeRegisterClosing_dateNotMatch(){
        CashRegisterRequestDTO dto = CashRegisterRequestDTO.builder().amountClosingCash(1500).amountClosingPos(500)
                .comment("Test comment").build();
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        doNothing().when(validator).validateRoleCanRegister(false);
        CashRegister cashRegister = CashRegister.builder().user(user)
                .dateOpening(LocalDateTime.now().plusDays(2)).build();
        when(cashRegisterRepo.findLatestOpenRegisterByUser(user.getId())).thenReturn(cashRegister);
        doThrow(new CashRegisterDateNotMatchException("La fecha no coincide con la apertura.")).when(validator)
                .validateCloseRegister(dto, cashRegister);

        CashRegisterDateNotMatchException e = assertThrows(CashRegisterDateNotMatchException.class,
                () -> cashRegisterService.closeRegister("johnny", dto));

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(validator, times(1)).validateRoleCanRegister(false);
        verify(cashRegisterRepo, times(1)).findLatestOpenRegisterByUser(user.getId());
        verify(cashRegisterRepo, never()).save(any(CashRegister.class));

        assertEquals("La fecha no coincide con la apertura.", e.getMessage());
    }

    //Prueba para obtener lista de cashRegister
    @Test
    void getCashRegisters(){
        CashRegister cashRegister = CashRegister.builder().user(user).amountOpening(1000).amountClosingCash(1500)
                .amountClosingPos(5000).status("CLOSED").alert(false).build();
        CashRegister cashRegister2 = CashRegister.builder().user(user).amountOpening(1000).amountClosingCash(100)
                .amountClosingPos(5000).status("CLOSED").alert(true).build();
        List<CashRegister> list = List.of(cashRegister, cashRegister2);
        when(cashRegisterRepo.findAll()).thenReturn(list);

        List<CashRegisterResponseDTO> result = cashRegisterService.getCashRegisters();
        verify(cashRegisterRepo, times(1)).findAll();

        assertEquals(list.size(), result.size());
    }

    //Prueba para actualizar los montos del registro existente
    @Test
    void updateRegister(){
        CashRegisterRequestDTO dto = CashRegisterRequestDTO.builder().id(1L).amountClosingCash(1000)
                .amountClosingPos(5000).amountOpening(500).build();
        CashRegister cashRegister = CashRegister.builder().id(1L).build();
        when(cashRegisterRepo.findById(dto.getId())).thenReturn(Optional.of(cashRegister));

        ArgumentCaptor<CashRegister> cashRegisterCaptor = ArgumentCaptor.forClass(CashRegister.class);
        cashRegisterService.updateRegister(dto);

        verify(cashRegisterRepo, times(1)).findById(dto.getId());
        verify(cashRegisterRepo, times(1)).save(cashRegisterCaptor.capture());

        CashRegister updatedRegister = cashRegisterCaptor.getValue();
        assertEquals(500, updatedRegister.getAmountOpening());
        assertEquals(1000, updatedRegister.getAmountClosingCash());
        assertEquals(5000, updatedRegister.getAmountClosingPos());
    }
    @Test
    void updateRegister_registerNotFound(){
        CashRegisterRequestDTO dto = CashRegisterRequestDTO.builder().id(1L).build();
        when(cashRegisterRepo.findById(1L)).thenReturn(Optional.empty());

        CashRegisterNotFoundException e = assertThrows(CashRegisterNotFoundException.class,
                () -> cashRegisterService.updateRegister(dto));

        verify(cashRegisterRepo, times(1)).findById(1L);
        verify(cashRegisterRepo, never()).save(any(CashRegister.class));

        assertEquals("Registro de caja no encontrado", e.getMessage());
    }

    //Prueba para verificar si un usuario tiene un registro abierto en la fecha actual
    @Test
    void hasOpenRegisterOnDate_true(){
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.of(user));
        when(cashRegisterRepo.existsOpenRegisterByUserAndDate(user.getId(), LocalDate.now())).thenReturn(true);

        boolean result = cashRegisterService.hasOpenRegisterOnDate("johnny");

        verify(userRepo, times(1)).findByUsername("johnny");
        verify(cashRegisterRepo, times(1)).
                existsOpenRegisterByUserAndDate(user.getId(), LocalDate.now());
        assertTrue(result);
    }
    @Test
    void hasOpenRegisterOnDate_false(){
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.of(user));
        when(cashRegisterRepo.existsOpenRegisterByUserAndDate(user.getId(), LocalDate.now())).thenReturn(false);

        boolean result = cashRegisterService.hasOpenRegisterOnDate("johnny");

        verify(userRepo, times(1)).findByUsername("johnny");
        verify(cashRegisterRepo, times(1)).
                existsOpenRegisterByUserAndDate(user.getId(), LocalDate.now());
        assertFalse(result);
    }
    @Test
    void hasOpenRegisterOnDate_NoUserFound(){
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.empty());

        boolean result = cashRegisterService.hasOpenRegisterOnDate("johnny");

        verify(userRepo, times(1)).findByUsername("johnny");
        verify(cashRegisterRepo, never()).existsOpenRegisterByUserAndDate(user.getId(), LocalDate.now());
        assertFalse(result);
    }
}
