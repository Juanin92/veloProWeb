package com.veloproweb.validation;

import com.veloproweb.exceptions.product.CategoryNotFoundException;
import com.veloproweb.exceptions.product.SubcategoryAlreadyExistsException;
import com.veloproweb.model.entity.product.SubcategoryProduct;
import org.springframework.stereotype.Component;

@Component
public class SubcategoryValidator {

    /**
     * Válida una subcategoría existe
     * @param subcategory - subcategoría
     */
    public void validateSubcategoryDoesNotExist(SubcategoryProduct subcategory){
        if (subcategory != null ){
            throw new SubcategoryAlreadyExistsException(
                    String.format("Nombre Existente: Hay registro de esta Subcategoría en la Categoría %s",
                            subcategory.getCategory().getName()));
        }
    }

    /**
     * Válida que una subcategoría tenga una categoría asociada
     * @param subcategory - subcategoría a validar
     */
    public void validateSubcategoryHasCategory(SubcategoryProduct subcategory){
        if (subcategory.getCategory() == null) {
            throw new CategoryNotFoundException("Dede seleccionar una categoría.");
        }
    }
}
