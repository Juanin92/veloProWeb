package com.veloProWeb.validation;

import com.veloProWeb.exceptions.sale.CashRegisterDateNotMatchException;
import com.veloProWeb.exceptions.sale.CashRegisterNotFoundException;
import com.veloProWeb.exceptions.sale.InvalidAmountCashRegisterException;
import com.veloProWeb.exceptions.sale.UnauthorizedCashRegisterAccessException;
import com.veloProWeb.model.entity.Sale.CashRegister;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CashRegisterValidatorTest {

    @InjectMocks private CashRegisterValidator validator;

    //Prueba para validar el monto de un valor
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, -100})
    void validateAmount(int value) {
        InvalidAmountCashRegisterException e = assertThrows(InvalidAmountCashRegisterException.class,
                () -> validator.validateAmount(value));
        assertEquals("El monto de la caja debe ser mayor a 0", e.getMessage());
    }

    //Prueba para validar si un registro de caja existe
    @Test
    void validateCashRegisterExists() {
        CashRegisterNotFoundException e = assertThrows(CashRegisterNotFoundException.class,
                () -> validator.validateCashRegisterExists(null));
        assertEquals("No hay registro de apertura válido.", e.getMessage());
    }

    //Prueba para validar la fecha de apertura de un registro de caja
    @Test
    void validateCashRegisterDate() {
        LocalDateTime date = LocalDateTime.now().plusDays(10);
        CashRegister cashRegister = CashRegister.builder().dateOpening(date).build();
        CashRegisterDateNotMatchException e = assertThrows(CashRegisterDateNotMatchException.class,
                () -> validator.validateCashRegisterDate(cashRegister));
        assertEquals("La fecha no coincide con la apertura.", e.getMessage());
    }

    @Test
    void validateRoleCanRegister() {
        UnauthorizedCashRegisterAccessException e = assertThrows(UnauthorizedCashRegisterAccessException.class,
                () -> validator.validateRoleCanRegister(false));
        assertEquals("Este rol no puede operar con registros de caja", e.getMessage());
    }
}