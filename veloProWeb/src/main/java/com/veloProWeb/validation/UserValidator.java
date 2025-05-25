package com.veloProWeb.validation;

import com.veloProWeb.exceptions.user.*;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.model.Enum.Rol;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validateUserDoesNotExist(User user){
        if (user != null){
            throw new UserAlreadyExistsException("Usuario Existente: Ya hay existe el usuario");
        }
    }

    public void validateUserExists(User user){
        if (user == null) {
            throw new UserNotFoundException("No existe registro del usuario");
        }
    }

    public void isDeleted(boolean status){
        if (!status){
            throw new UserAlreadyDeletedException("Usuario ya ha sido desactivado");
        }
    }

    public void isActivated(boolean status){
        if (status){
            throw new UserAlreadyActivatedException("Usuario ya ha sido activado");
        }
    }

    public void validateRole(Rol role){
        if (role.equals(Rol.MASTER)){
            throw new UserMasterRoleSelectedException("rol maestro ya fue seleccionado");
        }
    }

    /**
     * Válida los datos del usuario
     * @param user - Usurario seleccionado
     */
    public void validate(User user){
        validatePassword(user.getPassword());
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
