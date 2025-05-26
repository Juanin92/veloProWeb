package com.veloProWeb.validation;

import com.veloProWeb.exceptions.user.*;
import com.veloProWeb.model.dto.user.UpdateUserDTO;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.model.Enum.Rol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @InjectMocks private UserValidator validator = new UserValidator();

    //Prueba para validar que el usuario no exista
    @Test
    void validateUserDoesNotExist() {
        User user = User.builder().build();
        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () ->  validator.validateUserDoesNotExist(user));
        assertEquals("Usuario ya registrado", ex.getMessage());
    }

    //Prueba para validar que el usuario existe
    @Test
    void validateUserExists() {
        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () ->  validator.validateUserExists(null));
        assertEquals("No existe registro del usuario", ex.getMessage());
    }

    //Prueba para validar que el usuario no esté eliminado
    @Test
    void validateIsNotDeleted() {
        UserAlreadyDeletedException ex = assertThrows(UserAlreadyDeletedException.class,
                () -> validator.validateIsNotDeleted(false));
        assertEquals("Usuario ya está inactivo", ex.getMessage());
    }

    //Prueba para validar que el usuario no esté activado
    @Test
    void validateIsActivated() {
        UserAlreadyActivatedException ex = assertThrows(UserAlreadyActivatedException.class,
                () -> validator.validateIsActivated(true));
        assertEquals("Usuario ya activado", ex.getMessage());
    }

    //Prueba para validar que el rol no sea maestro
    @Test
    void validateRoleIsNotMaster() {
        UserMasterRoleSelectedException ex = assertThrows(UserMasterRoleSelectedException.class,
                () -> validator.validateRoleIsNotMaster(Rol.MASTER));
        assertEquals("El rol maestro ya ha sido asignado", ex.getMessage());
    }

    //Prueba para validar que el nombre de usuario no esté registrado
    @Test
    void validateUpdateUser_usernameExists() {
        User user = User.builder().username("johnny").build();
        UpdateUserDTO dto = UpdateUserDTO.builder().username("johnnie").build();
        UsernameAlreadyExistsException ex = assertThrows(UsernameAlreadyExistsException.class,
                () -> validator.validateUpdateUser(user, dto, true, false));
        assertEquals("El nombre de usuario ya está en uso", ex.getMessage());
    }

    //Prueba para validar que el email no esté registrado
    @Test
    void validateUpdateUser_emailExists() {
        User user = User.builder().username("johnny").email("test@test.com").build();
        UpdateUserDTO dto = UpdateUserDTO.builder().username("johnny").email("test@test.cl").build();
        EmailAlreadyRegisterException ex = assertThrows(EmailAlreadyRegisterException.class,
                () -> validator.validateUpdateUser(user, dto, false, true));
        assertEquals("El email ya está registrado", ex.getMessage());
    }

    //Prueba para validar que la contraseña actual o el código de recuperación son correctos
    @Test
    void validateCurrentCredentials() {
        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                () -> validator.validateCurrentCredentials(false, false));
        assertEquals("La contraseña actual o el código de recuperación son incorrectos", ex.getMessage());
    }

    //Prueba para validar que las contraseñas nuevas coinciden
    @Test
    void validateNewPasswordMatch() {
        UpdateUserDTO dto = UpdateUserDTO.builder().newPassword("1234").currentPassword("3212").build();
        PasswordMismatchException ex = assertThrows(PasswordMismatchException.class,
                () -> validator.validateNewPasswordMatch(dto));
        assertEquals("Las contraseñas nuevas no coinciden", ex.getMessage());
    }
}
