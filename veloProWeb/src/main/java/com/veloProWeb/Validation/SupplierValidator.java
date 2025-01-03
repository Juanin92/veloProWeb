package com.veloProWeb.Validation;

import com.veloProWeb.Model.Entity.Purchase.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierValidator {

    /**
     * Válida los valores de un proveedor
     * @param supplier - Objeto que contiene los valores
     */
    public void validate(Supplier supplier){
        validateName(supplier.getName());
        validatePhone(supplier.getPhone());
        validateEmail(supplier.getEmail());
        validateRut(supplier.getRut());
    }

    /**
     * Válida el nombre del proveedor, lanzará una excepción
     * Si el nombre es nulo, la cadena está vacía o el largo es menor a 3 caracteres
     * @param name - cadena que contiene el nombre
     */
    private void validateName(String name){
        if (name == null || name.trim().isBlank() || name.trim().length() < 3) {
            throw new IllegalArgumentException("Ingrese un nombre válido.");
        }
    }

    /**
     * Válida el número de teléfono, lanzará una excepción
     * si la cadena es nula, vacía o el largo es diferente a 13 caracteres
     * @param phone - cadena que contiene el número de teléfono
     */
    private void validatePhone(String phone){
        if (phone == null || phone.trim().isBlank() || phone.trim().length() != 13) {
            throw new IllegalArgumentException("Ingrese número válido, Ej: +569 12345678");
        }
    }

    /**
     * Válida el email del proveedor, lanzará una excepción
     * si cadena esta vacía o el formato no corresponde a un email
     * @param email - cadena que contiene el valor de email
     */
    private void validateEmail(String email){
        if (email.trim().isBlank() || !email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            throw new IllegalArgumentException("Ingrese Email válido.");
        }
    }

    /**
     * Válida el rut de un proveedor, lanzará una excepción
     * si la cadena está vacía o el formato de la cadena no corresponde a un rut
     * @param rut - cadena que contiene el rut del proveedor
     */
    private void validateRut(String rut){
        if (rut.trim().isBlank() || !rut.matches("^\\d{7,8}-\\d|[kK]$")) {
            throw new IllegalArgumentException("Rut no es correcto, Ingrese un formato válido");
        }
    }
}
