package com.veloProWeb.Validation;

import com.veloProWeb.Model.Entity.Customer.Customer;
import org.springframework.stereotype.Component;


@Component
public class CustomerValidator {

    /**
     * Válida los datos de un cliente
     * @param customer - Cliente seleccionado a validar
     */
    public void validate(Customer customer){
        validateName(customer.getName());
        validateSurname(customer.getSurname());
        validatePhone(customer.getPhone());
        validateEmail(customer.getEmail());
    }

    /**
     * Válida el monto del pago en relación con la deuda del cliente.
     * Si el pago es menor o igual a cero y si el pago es mayor a la deuda del cliente,
     * se lanzará una excepción.
     * @param number - monto del pago
     * @param customer - Cliente seleccionado para evaluar la deuda
     */
    public void validateValuePayment(int number, Customer customer){
        if (number <= 0){
            throw new IllegalArgumentException("El monto no puede ser menor a 0.");
        }
        if (number > customer.getDebt()){
            throw new IllegalArgumentException("El monto supera el valor de la deuda.");
        }
    }

    /**
     * Válida un nombre,
     * la cadena no de ser nula, no estar vacía, menor a 3 caracteres o contenga solo letras.
     * Si no lanzara una excepción
     * @param name - cadena que contiene el nombre
     */
    private void validateName(String name){
        if (name == null || name.trim().isBlank() || name.trim().length() < 3 || !name.matches("[a-zA-Z ]+")){
            throw new IllegalArgumentException("Ingrese nombre válido.");
        }
    }

    /**
     * Válida un apellido,
     * que la cadena no sea nula, este vacía, tenga un largo menor a 3 caracteres o solo contenga letras.
     * La cadena debe tener 2 apellidos, comprueba que haya una separación entre palabras.
     * Si no lanza una excepción
     * @param surname - cadena que contiene el apellido
     */
    private void validateSurname(String surname){
        if (surname == null || surname.trim().isBlank() || surname.trim().length() < 3 || !surname.matches("[a-zA-Z ]+")){
            throw new IllegalArgumentException("Ingrese apellido válido.");
        }
        String[] words = surname.trim().split("\\s+");
        if (words.length != 2) {
            throw new IllegalArgumentException("Ingrese los 2 apellidos");
        }
    }

    /**
     * Válida un número de teléfono,
     * que la cadena no este vacía o el largo de la cadena sea diferente a 13 caracteres, si no lanza una excepción
     * @param phone - cadena que contiene el número de teléfono
     */
    private void validatePhone(String phone){
        if (phone.trim().isBlank() || phone.trim().length() != 13){
            throw new IllegalArgumentException("Ingrese número válido, Ej: +569 12345678");
        }
    }

    /**
     * Válida un email,
     * que la cadena no este vacía o tenga un formato de email, si no lanza una excepción
     * @param email - cadena que contiene el email
     */
    private void validateEmail(String email){
        if (email.trim().isBlank() || !email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")){
            throw new IllegalArgumentException("Ingrese Email válido.");
        }
    }
}
