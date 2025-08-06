package com.veloproweb.validation;

import com.veloproweb.exceptions.purchase.PurchaseNotFoundException;
import com.veloproweb.exceptions.purchase.PurchaseTotalMismatchException;
import com.veloproweb.model.dto.purchase.PurchaseDetailRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PurchaseValidatorTest {
    @InjectMocks private PurchaseValidator validator;

    //Prueba para validar la existencia de una compra
    @Test
    void hasPurchase_valid(){
        PurchaseNotFoundException exception = assertThrows(PurchaseNotFoundException.class,
                () -> validator.hasPurchase(null));
        assertEquals("Compra no encontrada", exception.getMessage());
    }

    //Prueba para validar el total de la compra
    @Test
    void validateTotal_valid(){
        PurchaseDetailRequestDTO dto = PurchaseDetailRequestDTO.builder().total(1000).build();
        PurchaseDetailRequestDTO dto2 = PurchaseDetailRequestDTO.builder().total(1000).build();
        List<PurchaseDetailRequestDTO> details = List.of(dto, dto2);

        PurchaseTotalMismatchException e = assertThrows(PurchaseTotalMismatchException.class,
                ()-> validator.validateTotal(3000, details));
        assertEquals("""
                    Valor de total de compra no coinciden \
                    
                    total compra: 3000\s
                    total de detalle: 2000""", e.getMessage());
    }
}
