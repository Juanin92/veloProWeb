package com.veloProWeb.Validation;

import com.veloProWeb.Model.Entity.Costumer.Costumer;
import org.springframework.stereotype.Component;

@Component
public class CostumerValidator {
    public void validate(Costumer costumer){
        validateName(costumer.getName());
        validateSurname(costumer.getSurname());
        validatePhone(costumer.getPhone());
        validateEmail(costumer.getEmail());
    }
    public void validateValuePayment(int number, Costumer costumer){
        if (number <= 0){
            throw new IllegalArgumentException("El monto no puede ser menor a 0.");
        }
        if (number > costumer.getDebt()){
            throw new IllegalArgumentException("El monto supera el valor de la deuda.");
        }
    }

    private void validateName(String name){
        if (name == null || name.trim().isBlank() || name.trim().length() < 3 || !name.matches("[a-zA-Z ]+")){
            throw new IllegalArgumentException("Ingrese nombre válido.");
        }
    }
    private void validateSurname(String surname){
        if (surname == null || surname.trim().isBlank() || surname.trim().length() < 3 || !surname.matches("[a-zA-Z ]+")){
            throw new IllegalArgumentException("Ingrese apellido válido.");
        }
    }
    private void validatePhone(String phone){
        if (phone.trim().isBlank() || phone.trim().length() != 13){
            throw new IllegalArgumentException("Ingrese número válido, Ej: +569 12345678");
        }
    }
    private void validateEmail(String email){
        if (email.trim().isBlank() || !email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")){
            throw new IllegalArgumentException("Ingrese Email válido.");
        }
    }
}
