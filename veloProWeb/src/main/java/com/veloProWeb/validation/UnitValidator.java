package com.veloProWeb.validation;

import com.veloProWeb.exceptions.product.UnitAlreadyExistsException;
import com.veloProWeb.exceptions.validation.ValidationException;
import com.veloProWeb.model.entity.Product.UnitProduct;
import org.springframework.stereotype.Component;

@Component
public class UnitValidator {

    /**
     * Válida una unidad de medida.
     * @param unit - unidad de medida
     */
    public void validateUnitDoesNotExist(UnitProduct unit){
        if (unit != null){
            throw new UnitAlreadyExistsException("Nombre Existente: Hay registro de esta unidad de medida.");
        }
    }

    /**
     * Válida el nombre de una unidad de medida,
     * lanzará excepciones si el nombre tiene más de 2 dígitos y 2 letras.
     * @param unit - unidad de medida a validar
     */
    public void validateUnitNameFormat(UnitProduct unit){
        int digitCount = unit.getNameUnit().replaceAll("[^0-9]", "").length();
        int letterCount = unit.getNameUnit().replaceAll("[^a-zA-Z]", "").length();
        if (digitCount > 3 || letterCount > 3) {
            throw new ValidationException("El nombre debe tener máximo 2 dígitos y 2 letras.");
        }
    }
}
