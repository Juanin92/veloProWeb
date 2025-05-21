package com.veloProWeb.validation;

import com.veloProWeb.exceptions.purchase.PurchaseNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PurchaseValidatorTest {
    @InjectMocks private PurchaseValidator validator;

    //Prueba para validar la existencia de una compra
    @Test
    public void hasPurchase_valid(){
        PurchaseNotFoundException exception = assertThrows(PurchaseNotFoundException.class,
                () -> validator.hasPurchase(null));
        assertEquals("Compra no encontrada", exception.getMessage());
    }
}
