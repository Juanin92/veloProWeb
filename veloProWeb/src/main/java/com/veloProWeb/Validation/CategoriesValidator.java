package com.veloProWeb.Validation;

import org.springframework.stereotype.Component;

@Component
public class CategoriesValidator {

    public void validateBrand(String name){
        if (name == null || name.trim().isBlank() || name.trim().length() <= 2){
            throw new IllegalArgumentException("Ingrese un nombre válido.");
        }
    }
    public void validateCategory(String name){
        if (name == null || name.trim().isBlank() || name.trim().length() < 3){
            throw new IllegalArgumentException("Ingrese un nombre válido.");
        }
        if (name.matches(".*\\d.*")){
            throw new IllegalArgumentException("El nombre no debe contener dígitos.");
        }
    }
    public void validateSubcategory(String name){
        if (name == null || name.trim().isBlank() || name.trim().length() <= 3){
            throw new IllegalArgumentException("Ingrese un nombre válido.");
        }
        if (name.matches(".*\\d.*")){
            throw new IllegalArgumentException("El nombre no debe contener dígitos.");
        }
    }
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
