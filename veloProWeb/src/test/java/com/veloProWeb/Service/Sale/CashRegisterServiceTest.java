package com.veloProWeb.Service.Sale;

import com.veloProWeb.Model.Entity.Sale.CashRegister;
import com.veloProWeb.Model.Entity.User;
import com.veloProWeb.Repository.Sale.CashRegisterRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CashRegisterServiceTest {
    @InjectMocks private CashRegisterService cashRegisterService;
    @Mock private CashRegisterRepo cashRegisterRepo;
    private CashRegister cashRegister;
    private User user;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setId(1L);

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

    //Prueba para obtener lista de cashRegister
    @Test
    public void getAll_valid(){
        List<CashRegister> list = Collections.singletonList(cashRegister);
        when(cashRegisterRepo.findAll()).thenReturn(list);
        List<CashRegister> result = cashRegisterService.getAll();
        verify(cashRegisterRepo).findAll();
        assertEquals(list, result);
    }
}
