package com.veloProWeb.validation;

import com.veloProWeb.exceptions.product.CategoryNotFoundException;
import com.veloProWeb.exceptions.product.SubcategoryAlreadyExistsException;
import com.veloProWeb.model.entity.Product.SubcategoryProduct;
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
