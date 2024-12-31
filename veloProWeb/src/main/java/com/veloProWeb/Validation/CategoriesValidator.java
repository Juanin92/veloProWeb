package com.veloProWeb.Validation;

import org.springframework.stereotype.Component;

@Component
public class CategoriesValidator {

    /**
     * Válida el nombre de una marca, lanzará excepciones:
     * Si la cadena es nula, esta vacía, contiene 2 o menos caracteres.
     * @param name - cadena que contiene el nombre de la marca
     */
    public void validateBrand(String name){
        if (name == null || name.trim().isBlank() || name.trim().length() <= 2){
            throw new IllegalArgumentException("Ingrese un nombre válido.");
        }
    }

    /**
     * Válida el nombre de una categoría, lanzará excepciones:
     * Si la cadena es nula, esta vacía, contiene menos de 3 caracteres.
     * Si la cadena contiene dígitos.
     * @param name - cadena que contiene el nombre de la categoría
     */
    public void validateCategory(String name){
        if (name == null || name.trim().isBlank() || name.trim().length() < 3){
            throw new IllegalArgumentException("Ingrese un nombre válido.");
        }
        if (name.matches(".*\\d.*")){
            throw new IllegalArgumentException("El nombre no debe contener dígitos.");
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
     * Válida el nombre de una unidad de medida. Lanzará excepciones
     * Si la cadena es nula, esta vacía, contiene menos o igual 3 caracteres.
     * Si la cadena no contiene letras y números.
     * Si la cadena no contiene un espacio entre números y letras.
     * @param name - cadena que contiene el nombre de la unidad de medida
     */
    public void validateUnit(String name){
        if (name == null || name.trim().isBlank() || name.trim().length() <= 3){
            throw new IllegalArgumentException("Ingrese un nombre válido.");
        }
        if (!name.matches("[a-zA-Z0-9 ]+")){
            throw new IllegalArgumentException("El nombre debe contener solo letras y números.");
        }
        if (!name.matches("[0-9]+ [a-zA-Z]+")){
            throw new IllegalArgumentException("El nombre debe tener un espacio entre dígitos y letras.");
        }
    }
}
