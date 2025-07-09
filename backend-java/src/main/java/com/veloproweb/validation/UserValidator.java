package com.veloproweb.validation;

import com.veloproweb.exceptions.user.*;
import com.veloproweb.exceptions.validation.ValidationException;
import com.veloproweb.model.dto.user.UpdateUserDTO;
import com.veloproweb.model.entity.user.User;
import com.veloproweb.model.Enum.Rol;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    /**
     * Valida que el usuario no exista en la base de datos.
     * @param user - usuario a validar
     */
    public void validateUserDoesNotExist(User user){
        if (user != null){
            throw new UserAlreadyExistsException("Usuario ya registrado");
        }
    }

    /**
     * Valida que el usuario exista en al base de datos.
     * @param user - usuario a validar
     */
    public void validateUserExists(User user){
        if (user == null) {
            throw new UserNotFoundException("No existe registro del usuario");
        }
    }

    /**
     * Valida que el nombre de usuario no exista en al base de datos.
     * @param user - usuario a validar
     * @param username - nombre de usuario a validar
     */
    public void validateUsernameIsAvailable(User user, String username){
        if (user != null && user.getUsername().equalsIgnoreCase(username)){
            throw new UsernameAlreadyExistsException("El nombre de usuario ya está en uso");
        }
    }

    /**
     * Valida que el nombre de usuario no exista en al base de datos al actualizar.
     * @param user - usuario a validar
     * @param rut - rut a validar
     */
    public void validateUsernameIsAvailableForUpdate(User user, String rut) {
        if (user != null && !user.getRut().equalsIgnoreCase(rut)) {
            throw new UsernameAlreadyExistsException("El nombre de usuario ya está en uso por otro usuario");
        }
    }

    /**
     * Valida que el usuario no esté eliminado.
     * @param isDeleted - estado de eliminación del usuario
     */
    public void validateIsNotDeleted(boolean isDeleted){
        if (!isDeleted){
            throw new UserAlreadyDeletedException("Usuario ya está inactivo");
        }
    }

    /**
     * Valida que el usuario no esté activado.
     * @param isActivated - estado de activación del usuario
     */
    public void validateIsActivated(boolean isActivated){
        if (isActivated){
            throw new UserAlreadyActivatedException("Usuario ya activado");
        }
    }

    /**
     * Valida que el rol no sea maestro.
     * @param role - rol a validar
     */
    public void validateRoleIsNotMaster(Rol role){
        if (role.equals(Rol.MASTER)){
            throw new UserMasterRoleSelectedException("El rol maestro ya ha sido asignado");
        }
    }

    /**
     * Valida que el usuario tenga un nombre de usuario y email únicos al actualizar.
     * @param user - usuario a validar
     * @param dto - objeto con los datos del usuario
     * @param existByUsername - indica si el nombre de usuario ya existe
     * @param existByEmail - indica si el email ya está registrado
     */
    public void validateUpdateUser(User user, UpdateUserDTO dto, boolean existByUsername, boolean existByEmail){
        if (!user.getUsername().equals(dto.getUsername()) && existByUsername) {
            throw new UsernameAlreadyExistsException("El nombre de usuario ya está en uso");
        }
        if (!user.getEmail().equals(dto.getEmail()) && existByEmail) {
            throw new EmailAlreadyRegisterException("El email ya está registrado");
        }
    }

    /**
     * Valida que la contraseña actual o el código de recuperación sean correctos.
     * @param isCurrentPasswordValid - indica si la contraseña actual es válida
     * @param isTokenValid - indica si el código de recuperación es válido
     */
    public void validateCurrentCredentials(boolean isCurrentPasswordValid, boolean isTokenValid){
        if (!isCurrentPasswordValid && !isTokenValid) {
            throw new InvalidCredentialsException("La contraseña actual o el código de recuperación son incorrectos");
        }
    }

    /**
     * Valida que la nueva contraseña y la confirmación de contraseña coincidan.
     * @param dto - objeto con los datos de actualización del usuario
     */
    public void validateNewPasswordMatch(UpdateUserDTO dto){
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new PasswordMismatchException("Las contraseñas nuevas no coinciden");
        }
    }

    /**
     * Valida que las contraseñas cumplan los requisitos.
     * @param dto - objeto con los datos de actualización del usuario
     */
    public void validatePassword(UpdateUserDTO dto){
        if (dto.getCurrentPassword() == null || dto.getCurrentPassword().trim().isBlank()
                || dto.getCurrentPassword().length() <= 7){
            throw new ValidationException("Contraseña actual debe tener al menos 7 caracteres o no debe estar vacía");
        }
        if (dto.getNewPassword() == null || dto.getNewPassword().trim().isBlank() || dto.getNewPassword().length() <= 7)
        {
            throw new ValidationException("Nueva contraseña debe tener al menos 7 caracteres o no debe estar vacía");
        }
        if (dto.getConfirmPassword() == null || dto.getConfirmPassword().trim().isBlank()
                || dto.getConfirmPassword().length() <= 7){
            throw new ValidationException("Contraseña de confirmación debe tener al menos 7 caracteres " +
                    "o no debe estar vacía");
        }
    }

    /**
     * Verifica que el usuario no esté eliminado.
     * @param status - estado del usuario
     */
    public void validateUserIsNotDeleted(boolean status){
        if (!status){
            throw new UserAlreadyDeletedException("El usuario ha sido eliminado. No se puede realizar la operación.");
        }
    }

    /**
     * Verifica que el usuario tenga un rol asignado.
     * @param user - usuario a validar
     */
    public void validateUserHasRole(User user){
        if (user.getRole() == null) {
            throw new UserRoleNotFoundException("El usuario no tiene un rol asignado");
        }
    }
}
