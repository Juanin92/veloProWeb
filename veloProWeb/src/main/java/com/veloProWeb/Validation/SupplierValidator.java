package com.veloProWeb.Validation;

import com.veloProWeb.Model.Entity.Purchase.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierValidator {
    public void validate(Supplier supplier){
        validateName(supplier.getName());
        validatePhone(supplier.getPhone());
        validateEmail(supplier.getEmail());
        validateRut(supplier.getRut());
    }

    private void validateName(String name){
        if (name == null || name.trim().isBlank() || name.trim().length() < 3) {
            throw new IllegalArgumentException("Ingrese un nombre válido.");
        }
    }

    private void validatePhone(String phone){
        if (phone == null || phone.trim().isBlank() || phone.trim().length() != 13) {
            throw new IllegalArgumentException("Ingrese número válido, Ej: +569 12345678");
        }
    }

    private void validateEmail(String email){
        if (email.trim().isBlank() || !email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            throw new IllegalArgumentException("Ingrese Email válido.");
        }
    }

    private void validateRut(String rut){
        if (rut.trim().isBlank() || !rut.matches("^\\d{7,8}-\\d|[kK]$")) {
            throw new IllegalArgumentException("Rut no es correcto, Ingrese un formato válido");
        }
    }
}
