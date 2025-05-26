package com.veloProWeb.validation;

import com.veloProWeb.exceptions.user.*;
import com.veloProWeb.model.dto.user.UpdateUserDTO;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.model.Enum.Rol;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validateUserDoesNotExist(User user){
        if (user != null){
            throw new UserAlreadyExistsException("Usuario ya registrado");
        }
    }

    public void validateUserExists(User user){
        if (user == null) {
            throw new UserNotFoundException("No existe registro del usuario");
        }
    }

    public void validateIsNotDeleted(boolean isDeleted){
        if (!isDeleted){
            throw new UserAlreadyDeletedException("Usuario ya está inactivo");
        }
    }

    public void validateIsActivated(boolean isActivated){
        if (isActivated){
            throw new UserAlreadyActivatedException("Usuario ya activado");
        }
    }

    public void validateRoleIsNotMaster(Rol role){
        if (role.equals(Rol.MASTER)){
            throw new UserMasterRoleSelectedException("El rol maestro ya ha sido asignado");
        }
    }

    public void validateUpdateUser(User user, UpdateUserDTO dto, boolean existByUsername, boolean existByEmail){
        if (!user.getUsername().equals(dto.getUsername()) && existByUsername) {
            throw new UsernameAlreadyExistsException("El nombre de usuario ya está en uso");
        }
        if (!user.getEmail().equals(dto.getEmail()) && existByEmail) {
            throw new EmailAlreadyRegisterException("El email ya está registrado");
        }
    }

    public void validateCurrentCredentials(boolean isCurrentPasswordValid, boolean isTokenValid){
        if (!isCurrentPasswordValid && !isTokenValid) {
            throw new InvalidCredentialsException("La contraseña actual o el código de recuperación son incorrectos");
        }
    }

    public void validateNewPasswordMatch(UpdateUserDTO dto){
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new PasswordMismatchException("Las contraseñas nuevas no coinciden");
        }
    }
}
