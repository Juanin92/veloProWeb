package com.veloProWeb.validation;

import com.veloProWeb.exceptions.product.BrandAlreadyExistsException;
import com.veloProWeb.model.entity.product.BrandProduct;
import org.springframework.stereotype.Component;

@Component
public class BrandValidator {

    /**
     * Válida la existencia de una marca ya registrada
     * @param brand - marca a validar
     */
    public void validateBrandDoesNotExist(BrandProduct brand){
        if (brand != null ){
            throw new BrandAlreadyExistsException("Nombre Existente: Hay registro de esta marca.");
        }
    }
}
