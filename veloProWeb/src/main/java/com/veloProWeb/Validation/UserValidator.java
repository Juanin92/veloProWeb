package com.veloProWeb.Validation;

import com.veloProWeb.Model.Entity.User.User;
import com.veloProWeb.Model.Enum.Rol;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    /**
     * Válida el estado actual del usuario
     * @param status - estado del usuario
     */
    public void validateStatus(boolean status){
        if (!status){
            throw new IllegalArgumentException("Usuario ha sido desactivado");
        }
    }

    /**
     * Válida los datos del usuario
     * @param user - Usurario seleccionado
     */
    public void validate(User user){
        validateRole(user.getRole());
        validateRut(user.getRut());
        validateName(user.getName());
        validateSurname(user.getSurname());
        validateEmail(user.getEmail());
        validateUsername(user.getUsername());
        validatePassword(user.getPassword());
    }

    /**
     * Válida el rol del usuario.
     * No debe ser nulo
     * @param role - Rol asignado al cliente
     */
    private void validateRole(Rol role){
        if (role == null){
            throw new IllegalArgumentException("Seleccione un rol para el usuario");
        }
    }

    /**
     * Válida el rut del usuario.
     * No debe estar vació y debe tener un formato establecido de rut
     * @param rut - cadena contiene el rut
     */
    private void validateRut(String rut){
        if (rut.trim().isBlank() || !rut.matches("^\\d{7,8}-[\\dKk]$")){
            throw new IllegalArgumentException("El rut no es correcto.");
        }
    }

    /**
     * Válida el nombre del usuario.
     * No debe ser nulo, estar vació, el largo menor a 3 caracteres y el formato debe contener solo letras.
     * @param name - cadena contiene el nombre
     */
    private void validateName(String name){
        if (name == null || name.trim().isBlank() || name.trim().length() < 3 || !name.matches("[a-zA-Z ]+")){
            throw new IllegalArgumentException("Ingrese nombre válido.");
        }
    }

    /**
     * Válida el apellido del usuario.
     * No debe ser nulo, estar vació, el largo menor a 3 caracteres y el formato debe contener solo letras.
     * @param surname - cadena contiene el apellido
     */
    private void validateSurname(String surname){
        if (surname == null || surname.trim().isBlank() || surname.trim().length() < 3 || !surname.matches("[a-zA-Z ]+")){
            throw new IllegalArgumentException("Ingrese apellido válido.");
        }
    }

    /**
     * Válida el email del usuario.
     * No debe estar vació y si no tiene el formato de correo electrónico
     * @param email - cadena contiene el email
     */
    private void validateEmail(String email){
        if (email.trim().isBlank() || !email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")){
            throw new IllegalArgumentException("Ingrese Email válido.");
        }
    }

    /**
     * Válida el nombre de usuario del usuario.
     * No debe ser nulo, estar vació y ser el largo menor a 3 caracteres
     * @param username - cadena contiene el nombre de usuario
     */
    private void validateUsername(String username){
        if (username == null || username.trim().isBlank() || username.trim().length() < 3){
            throw new IllegalArgumentException("Ingrese nombre de usuario.");
        }
    }

    /**
     * Válida la contraseña del usuario.
     * No debe ser nulo, estar vació y ser el largo menor o igual a 7 caracteres
     * @param password - cadena contiene contraseña
     */
    private void validatePassword(String password){
        if (password == null || password.trim().isBlank() || password.trim().length() <= 7){
            throw new IllegalArgumentException("Ingrese contraseña válido. (Debe tener 8 o más caracteres o números)");
        }
    }
}
