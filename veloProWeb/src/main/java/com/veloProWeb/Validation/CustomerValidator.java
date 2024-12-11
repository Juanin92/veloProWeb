package com.veloProWeb.Validation;

import com.veloProWeb.Model.Entity.Customer.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidator {
    public void validate(Customer customer){
        validateName(customer.getName());
        validateSurname(customer.getSurname());
        validatePhone(customer.getPhone());
        validateEmail(customer.getEmail());
    }
    public void validateValuePayment(int number, Customer customer){
        if (number <= 0){
            throw new IllegalArgumentException("El monto no puede ser menor a 0.");
        }
        if (number > customer.getDebt()){
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
