package com.veloProWeb.validation;

import com.veloProWeb.model.entity.Purchase.Purchase;
import com.veloProWeb.model.entity.Purchase.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PurchaseValidatorTest {
    @InjectMocks private PurchaseValidator validator;
    @Mock private Supplier supplier;
    private Purchase purchase;

    @BeforeEach
    void setUp(){
        purchase = new Purchase();
        purchase.setId(1L);
        purchase.setDocument("A001");
        purchase.setDocumentType("Boleta");
        purchase.setIva(15000);
        purchase.setPurchaseTotal(20000);
        purchase.setDate(LocalDate.now());
        purchase.setSupplier(supplier);
    }

    //Prueba para validar un objeto purchase
    @Test
    public void validatePurchase_valid(){
        validator.validate(purchase);
    }

    //Prueba para validar la fecha de una compra
    @Test
    public void validateDate_valid(){
        purchase.setDate(LocalDate.of(2020, 4,20));
        validator.validate(purchase);
    }
    @Test
    public void validateDate_invalidNull(){
        purchase.setDate(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(purchase));
        assertEquals("Ingrese una fecha", exception.getMessage());
    }

    //Prueba para validar la existencia de proveedor en una compra
    @Test
    public void validateSupplier_valid(){
        validator.validate(purchase);
    }
    @Test
    public void validateSupplier_invalidNull(){
        purchase.setSupplier(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(purchase));
        assertEquals("Seleccione un proveedor", exception.getMessage());
    }

    //Prueba para validar el documento de una compra
    @Test
    public void validateDocument_valid(){
        purchase.setDocument("Prueba");
        validator.validate(purchase);
    }
    @Test
    public void validateDocument_invalidNull(){
        purchase.setDocument(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(purchase));
        assertEquals("Ingrese número de documento", exception.getMessage());
    }
    @Test
    public void validateDocument_invalidIsBlank(){
        purchase.setDocument(" ");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(purchase));
        assertEquals("Ingrese número de documento", exception.getMessage());
    }

    //Prueba para validar el monto total de una compra
    @Test
    public void validateTotal_valid(){
        purchase.setPurchaseTotal(2000);
        validator.validate(purchase);
    }
    @ParameterizedTest
    @ValueSource( ints = {0, -1})
    public void validateTotal_invalid(int value){
        purchase.setPurchaseTotal(value);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> validator.validate(purchase));
        assertEquals("Ingrese sólo números", exception.getMessage());
    }
}
