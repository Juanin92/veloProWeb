package com.veloproweb.validation;

import com.veloproweb.exceptions.user.*;
import com.veloproweb.exceptions.validation.ValidationException;
import com.veloproweb.model.dto.user.UpdateUserDTO;
import com.veloproweb.model.entity.user.User;
import com.veloproweb.model.Enum.Rol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    //prueba para validar que el nombre de usuario esté disponible
    @Test
    void validateUsernameIsAvailable() {
        User user = User.builder().username("Johnny").build();
        UsernameAlreadyExistsException ex = assertThrows(UsernameAlreadyExistsException.class,
                () -> validator.validateUsernameIsAvailable(user, "johnny"));
        assertEquals("El nombre de usuario ya está en uso", ex.getMessage());
    }

    //Prueba para validar que el nombre de usuario esté disponible para actualizar
    @Test
    void validateUsernameIsAvailableForUpdate() {
        User user = User.builder().username("Johnny").rut("12345678-9").build();
        UsernameAlreadyExistsException ex = assertThrows(UsernameAlreadyExistsException.class,
                () -> validator.validateUsernameIsAvailableForUpdate(user, "12345432-0"));
        assertEquals("El nombre de usuario ya está en uso por otro usuario", ex.getMessage());
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

    //Prueba para validar contraseñas
    @ParameterizedTest
    @ValueSource(strings = {"1234567", ""})
    void validatePassword_currentPassword(String currentPassword) {
        UpdateUserDTO dto = UpdateUserDTO.builder().currentPassword(currentPassword)
                .newPassword("password").confirmPassword("password").build();

        ValidationException e = assertThrows(ValidationException.class, () -> validator.validatePassword(dto));
        assertEquals("Contraseña actual debe tener al menos 7 caracteres o no debe estar vacía",
                e.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"1234567", ""})
    void validatePassword_newPassword(String newPassword) {
        UpdateUserDTO dto = UpdateUserDTO.builder().newPassword(newPassword)
                .currentPassword("password").confirmPassword("password").build();

        ValidationException e = assertThrows(ValidationException.class, () -> validator.validatePassword(dto));
        assertEquals("Nueva contraseña debe tener al menos 7 caracteres o no debe estar vacía",
                e.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"1234567", ""})
    void validatePassword_confirmPassword(String confirmPassword) {
        UpdateUserDTO dto = UpdateUserDTO.builder().confirmPassword(confirmPassword)
                .newPassword("password").currentPassword("password").build();

        ValidationException e = assertThrows(ValidationException.class, () -> validator.validatePassword(dto));
        assertEquals("Contraseña de confirmación debe tener al menos 7 caracteres o no debe estar vacía",
                e.getMessage());
    }

    //Prueba para validar que el usuario no esté eliminado
    @Test
    void validateUserIsNotDeleted() {
        User user = User.builder().status(false).build();
        UserAlreadyDeletedException e = assertThrows(UserAlreadyDeletedException.class,
                () -> validator.validateUserIsNotDeleted(user.isStatus()));

        assertEquals("El usuario ha sido eliminado. No se puede realizar la operación.", e.getMessage());
    }

    //Prueba para validar que el usuario tenga un rol asignado
    @Test
    void validateUserHasRole() {
        User user = User.builder().role(null).build();
        UserRoleNotFoundException e = assertThrows(UserRoleNotFoundException.class,
                () -> validator.validateUserHasRole(user));

        assertEquals("El usuario no tiene un rol asignado", e.getMessage());
    }
}
