package com.veloProWeb.validation;

import com.veloProWeb.exceptions.product.*;
import com.veloProWeb.model.entity.product.CategoryProduct;
import org.springframework.stereotype.Component;

@Component
public class CategoryValidator {

    /**
     * Válida la existencia de una categoría ya registrada
     * @param category - categoría a validar
     */
    public void validateCategoryDoesNotExist(CategoryProduct category){
        if (category != null ){
            throw new CategoryAlreadyExistsException("Nombre Existente: Hay registro de esta categoría.");
        }
    }
}
