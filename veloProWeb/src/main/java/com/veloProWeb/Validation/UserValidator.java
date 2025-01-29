package com.veloProWeb.Validation;

import com.veloProWeb.Model.Entity.User;
import com.veloProWeb.Model.Enum.Rol;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    public void validateStatus(boolean status){
        if (!status){
            throw new IllegalArgumentException("Usuario ha sido desactivado");
        }
    }
    public void validate(User user){
        validateRole(user.getRole());
        validateRut(user.getRut());
        validateName(user.getName());
        validateSurname(user.getSurname());
        validateEmail(user.getEmail());
        validateUsername(user.getUsername());
        validatePassword(user.getPassword());
    }

    private void validateRole(Rol role){
        if (role == null){
            throw new IllegalArgumentException("Seleccione un rol para el usuario");
        }
    }
    private void validateRut(String rut){
        if (rut.trim().isBlank() || !rut.matches("^\\d{7,8}-\\d|[kK]$")){
            throw new IllegalArgumentException("El rut no es correcto.");
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
    private void validateEmail(String email){
        if (email.trim().isBlank() || !email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")){
            throw new IllegalArgumentException("Ingrese Email válido.");
        }
    }
    private void validateUsername(String username){
        if (username == null || username.trim().isBlank() || username.trim().length() < 3){
            throw new IllegalArgumentException("Ingrese nombre de usuario.");
        }
    }
    private void validatePassword(String password){
        if (password == null || password.trim().isBlank() || password.trim().length() <= 7){
            throw new IllegalArgumentException("Ingrese contraseña válido. (Debe tener 8 o más caracteres o números)");
        }
    }
}
