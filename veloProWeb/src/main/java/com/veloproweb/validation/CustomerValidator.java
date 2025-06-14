package com.veloproweb.validation;

import com.veloproweb.exceptions.customer.CustomerAlreadyActivatedException;
import com.veloproweb.exceptions.customer.CustomerAlreadyDeletedException;
import com.veloproweb.exceptions.customer.CustomerAlreadyExistsException;
import com.veloproweb.exceptions.customer.InvalidPaymentAmountException;
import com.veloproweb.exceptions.validation.ValidationException;
import com.veloproweb.model.entity.customer.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidator {

    /**
     * Válida la info del cliente
     * @param customer - cliente seleccionado
     */
    public void validateInfoCustomer(Customer customer){
        validateEmail(customer.getEmail());
        validateSurnameLong(customer.getSurname());
    }

    /**
     * Válida que el cliente antes de eliminar
     * @param customer - cliente seleccionado
     */
    public void deleteCustomer(Customer customer){
        isNotActive(customer);
        hasDebt(customer);
    }

    /**
     * Válida que el cliente no exista en la DB
     * @param customer - cliente seleccionado
     */
    public void existCustomer(Customer customer){
        if (customer != null){
            throw new CustomerAlreadyExistsException("Cliente Existente: Hay registro de este cliente.");
        }
    }

    /**
     * Válida que el cliente tenga su cuenta activada
     * @param customer - cliente seleccionado
     */
    public void isActive(Customer customer){
        if (customer.isAccount()) {
            throw new CustomerAlreadyActivatedException("El cliente tiene su cuenta activada");
        }
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
            throw new InvalidPaymentAmountException("El monto no puede ser menor a 0.");
        }
        if (number > customer.getDebt()){
            throw new InvalidPaymentAmountException("El monto supera el valor de la deuda.");
        }
    }

    /**
     * Válida que el cliente no tenga deuda pendiente
     * @param customer - cliente seleccionado
     */
    private void hasDebt(Customer customer){
        if (customer.getDebt() > 0){
            throw new ValidationException("El cliente tiene deuda pendiente, no se puede eliminar.");
        }
    }

    /**
     * Válida que el cliente no haya sido eliminado anteriormente
     * @param customer - cliente seleccionado
     */
    private void isNotActive(Customer customer){
        if (!customer.isAccount()) {
            throw new CustomerAlreadyDeletedException("Cliente ya ha sido eliminado anteriormente.");
        }
    }

    /**
     * Válida la cadena contenga 2 apellidos
     * @param surname - cadena que contiene el apellido
     */
    private void validateSurnameLong(String surname){
        String[] words = surname.trim().split("\\s+");
        if (words.length != 2) {
            throw new ValidationException("Ingrese los 2 apellidos");
        }
    }

    /**
     * Válida un email,
     * que la cadena no este vacía o tenga un formato de email, si no lanza una excepción
     * @param email - cadena que contiene el email
     */
    private void validateEmail(String email){
        if (email.trim().isBlank() || !email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")){
            throw new ValidationException("Ingrese Email válido.");
        }
    }
}
