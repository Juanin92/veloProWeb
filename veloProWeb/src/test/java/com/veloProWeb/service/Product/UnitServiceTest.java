package com.veloProWeb.service.Product;

import com.veloProWeb.model.entity.Product.UnitProduct;
import com.veloProWeb.repository.Product.UnitProductRepo;
import com.veloProWeb.util.HelperService;
import com.veloProWeb.validation.CategoriesValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UnitServiceTest {

    @InjectMocks private UnitService unitService;
    @Mock private UnitProductRepo unitRepo;
    @Mock private CategoriesValidator validator;
    @Mock private HelperService helperService;
    private UnitProduct unit;
    private UnitProduct existingUnit;

    @BeforeEach
    void setUp(){
        unit = new UnitProduct();
        existingUnit = new UnitProduct();
    }

    //Prueba para crear una nueva unidad de medida
    @Test
    public void save_valid(){
        unit.setNameUnit("1 KG");
        doNothing().when(validator).validateUnit("1 KG");
        when(unitRepo.findByNameUnit("1 KG")).thenReturn(Optional.empty());
        when(helperService.upperCaseWord("1 KG")).thenReturn("1 KG");
        unitService.save(unit);

        verify(validator).validateUnit("1 KG");
        verify(unitRepo).findByNameUnit("1 KG");
        verify(unitRepo).save(unit);
        assertEquals("1 KG", unit.getNameUnit());
    }
    @Test
    public void save_invalidExistingUnit(){
        unit.setNameUnit("2 KG");
        existingUnit.setNameUnit("2 KG");
        doNothing().when(validator).validateUnit("2 KG");
        when(unitRepo.findByNameUnit("2 KG")).thenReturn(Optional.of(existingUnit));
        when(helperService.upperCaseWord("2 KG")).thenReturn("2 KG");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unitService.save(unit);
        });

        assertEquals("Nombre Existente: Hay registro de esta unidad de medida.", exception.getMessage());
        verify(validator).validateUnit("2 KG");
        verify(unitRepo).findByNameUnit("2 KG");
        verify(unitRepo, never()).save(unit);
    }
    @Test
    public void save_invalidNameLong(){
        unit.setNameUnit("2 KILO");
        doNothing().when(validator).validateUnit("2 KILO");
        when(unitRepo.findByNameUnit("2 KILO")).thenReturn(Optional.empty());
        when(helperService.upperCaseWord("2 KILO")).thenReturn("2 KILO");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unitService.save(unit);
        });

        assertEquals("El nombre debe tener máximo 2 dígitos y 2 letras.", exception.getMessage());
        verify(validator).validateUnit("2 KILO");
        verify(unitRepo).findByNameUnit("2 KILO");
        verify(unitRepo, never()).save(unit);
    }
    @Test
    public void save_invalidNumberLong(){
        unit.setNameUnit("200 KG");
        doNothing().when(validator).validateUnit("200 KG");
        when(unitRepo.findByNameUnit("200 KG")).thenReturn(Optional.empty());
        when(helperService.upperCaseWord("200 KG")).thenReturn("200 KG");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unitService.save(unit);
        });

        assertEquals("El nombre debe tener máximo 2 dígitos y 2 letras.", exception.getMessage());
        verify(validator).validateUnit("200 KG");
        verify(unitRepo).findByNameUnit("200 KG");
        verify(unitRepo, never()).save(unit);
    }

    //Prueba para obtener todas las unidades de medida
    @Test
    public void getAll_valid(){
        unitService.getAll();
        verify(unitRepo).findAll();
    }
}
