package com.veloProWeb.validation;

import com.veloProWeb.exceptions.product.BrandAlreadyExistsException;
import com.veloProWeb.exceptions.product.CategoryAlreadyExistsException;
import com.veloProWeb.exceptions.product.UnitAlreadyExistsException;
import com.veloProWeb.exceptions.validation.ValidationException;
import com.veloProWeb.model.entity.Product.BrandProduct;
import com.veloProWeb.model.entity.Product.CategoryProduct;
import com.veloProWeb.model.entity.Product.UnitProduct;
import org.springframework.stereotype.Component;

@Component
public class CategoriesValidator {

    /**
     * Válida la existencia de una marca ya registrada
     * @param brand - marca a validar
     */
    public void validateBrand(BrandProduct brand){
        if (brand != null ){
            throw new BrandAlreadyExistsException("Nombre Existente: Hay registro de esta marca.");
        }
    }

    /**
     * Válida la existencia de una categoría ya registrada
     * @param category - categoría a validar
     */
    public void validateCategory(CategoryProduct category){
        if (category != null ){
            throw new CategoryAlreadyExistsException("Nombre Existente: Hay registro de esta categoría.");
        }
    }

    /**
     * Válida el nombre de una subcategoría, lanzará excepciones:
     * Si la cadena es nula, esta vacía, contiene 3 o menos caracteres.
     * Si la cadena contiene dígitos.
     * @param name - cadena que contiene el nombre de la subcategoría
     */
    public void validateSubcategory(String name){
        if (name == null || name.trim().isBlank() || name.trim().length() <= 3){
            throw new IllegalArgumentException("Ingrese un nombre válido.");
        }
        if (name.matches(".*\\d.*")){
            throw new IllegalArgumentException("El nombre no debe contener dígitos.");
        }
    }

    /**
     * Válida una unidad de medida.
     * @param unit - unidad de medida
     */
    public void validateUnit(UnitProduct unit){
        if (unit != null){
            throw new UnitAlreadyExistsException("Nombre Existente: Hay registro de esta unidad de medida.");
        }
    }

    /**
     * Válida el nombre de una unidad de medida,
     * lanzará excepciones si el nombre tiene más de 2 dígitos y 2 letras.
     * @param unit - unidad de medida a validar
     */
    public void validateUnitName(UnitProduct unit){
        int digitCount = unit.getNameUnit().replaceAll("[^0-9]", "").length();
        int letterCount = unit.getNameUnit().replaceAll("[^a-zA-Z]", "").length();
        if (digitCount > 3 || letterCount > 3) {
            throw new ValidationException("El nombre debe tener máximo 2 dígitos y 2 letras.");
        }
    }
}
