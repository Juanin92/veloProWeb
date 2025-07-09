package com.veloproweb.service.product;

import com.veloproweb.exceptions.product.UnitAlreadyExistsException;
import com.veloproweb.exceptions.validation.ValidationException;
import com.veloproweb.model.entity.product.UnitProduct;
import com.veloproweb.repository.product.UnitProductRepo;
import com.veloproweb.validation.UnitValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @InjectMocks private UnitService unitService;
    @Mock private UnitProductRepo unitRepo;
    @Mock private UnitValidator validator;
    private UnitProduct unit, unitUn, unitGr, existingUnit;

    @BeforeEach
    void setUp(){
        unit = UnitProduct.builder().id(1L).nameUnit("10 KG").build();
        unitUn = UnitProduct.builder().id(2L).nameUnit("1 UN").build();
        unitGr = UnitProduct.builder().id(3L).nameUnit("50 GR").build();
        existingUnit = UnitProduct.builder().id(5L).nameUnit("35 KG").build();
    }

    //Prueba para crear una nueva unidad de medida
    @Test
    void save_valid(){
        when(unitRepo.findByNameUnit("10 KG")).thenReturn(Optional.empty());
        doNothing().when(validator).validateUnitDoesNotExist(null);
        doNothing().when(validator).validateUnitNameFormat(unit);

        unitService.save(unit);

        ArgumentCaptor<UnitProduct> unitProductArgumentCaptor = ArgumentCaptor.forClass(UnitProduct.class);
        verify(unitRepo, times(1)).findByNameUnit("10 KG");
        verify(unitRepo, times(1)).save(unitProductArgumentCaptor.capture());

        UnitProduct result = unitProductArgumentCaptor.getValue();
        assertEquals(result.getNameUnit(), unit.getNameUnit());
    }
    @Test
    void save_invalidExistingUnit(){
        when(unitRepo.findByNameUnit("35 KG")).thenReturn(Optional.of(existingUnit));
        doThrow(new UnitAlreadyExistsException("Nombre Existente: Hay registro de esta unidad de medida."))
                .when(validator).validateUnitDoesNotExist(existingUnit);

        UnitAlreadyExistsException exception = assertThrows(UnitAlreadyExistsException.class,
                () -> unitService.save(existingUnit));

        verify(unitRepo, times(1)).findByNameUnit("35 KG");
        verify(validator, times(1)).validateUnitDoesNotExist(existingUnit);
        verify(unitRepo, never()).save(existingUnit);
        assertEquals("Nombre Existente: Hay registro de esta unidad de medida.", exception.getMessage());
    }
    @Test
    void save_invalidNameException() {
        UnitProduct unit = UnitProduct.builder().nameUnit("35 kilogramos").build(); // entrada no válida
        when(unitRepo.findByNameUnit("35 KILOGRAMOS")).thenReturn(Optional.empty());

        doNothing().when(validator).validateUnitDoesNotExist(null);
        doThrow(new ValidationException("El nombre debe tener máximo 2 dígitos y 2 letras."))
                .when(validator).validateUnitNameFormat(unit);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> unitService.save(unit));

        verify(unitRepo).findByNameUnit("35 KILOGRAMOS");
        verify(validator).validateUnitDoesNotExist(null);
        verify(validator).validateUnitNameFormat(unit);
        verify(unitRepo, never()).save(any());

        assertEquals("El nombre debe tener máximo 2 dígitos y 2 letras.", exception.getMessage());
    }


    //Prueba para obtener todas las unidades de medida
    @Test
    void getAll_valid(){
        List<UnitProduct> units = List.of(unitGr, unit, existingUnit, unitUn);
        when(unitRepo.findAllOrderByNameAsc()).thenReturn(units);

        List<UnitProduct> result = unitService.getAll();

        verify(unitRepo, times(1)).findAllOrderByNameAsc();

        assertEquals(units.size(), result.size());
        assertEquals(List.of(unitGr, unit, existingUnit, unitUn), result);
    }
}
