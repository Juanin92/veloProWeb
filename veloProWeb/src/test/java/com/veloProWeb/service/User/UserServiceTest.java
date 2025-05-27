package com.veloProWeb.service.User;

import com.veloProWeb.exceptions.user.*;
import com.veloProWeb.mapper.UserMapper;
import com.veloProWeb.model.dto.user.UpdateUserDTO;
import com.veloProWeb.model.dto.user.UserRequestDTO;
import com.veloProWeb.model.dto.user.UserResponseDTO;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.model.Enum.Rol;
import com.veloProWeb.repository.UserRepo;
import com.veloProWeb.validation.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks private UserService userService;
    @Mock private UserRepo userRepo;
    @Mock private UserValidator validator;
    @Spy private BCryptPasswordEncoder passwordEncoder;
    @Mock private UserMapper mapper;

    //Prueba para crear usuario
    @Test
    public void createUser(){
        UserRequestDTO dto = UserRequestDTO.builder().name("john david").surname("doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).build();
        when(userRepo.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
        doNothing().when(validator).validateUsernameIsAvailable(null, dto.getUsername());
        doNothing().when(validator).validateRoleIsNotMaster(dto.getRole());
        when(userRepo.findByRut(dto.getRut())).thenReturn(Optional.empty());
        User userMapped = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(true).build();
        when(mapper.toEntity(dto)).thenReturn(userMapped);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        userService.createUser(dto);

        verify(userRepo, times(1)).findByUsername(dto.getUsername());
        verify(validator, times(1)).validateUsernameIsAvailable(null, dto.getUsername());
        verify(validator, times(1)).validateRoleIsNotMaster(dto.getRole());
        verify(userRepo, times(1)).findByRut(dto.getRut());
        verify(mapper, times(1)).toEntity(dto);
        verify(passwordEncoder, times(1)).encode("johnny19365");
        verify(userRepo, times(1)).save(userCaptor.capture());

        User resultUser = userCaptor.getValue();
        assertEquals("John David", resultUser.getName());
        assertEquals("Doe", resultUser.getSurname());
        assertEquals("johnny", resultUser.getUsername());
        assertTrue(passwordEncoder.matches("johnny19365", resultUser.getPassword()));
        assertEquals(LocalDate.now(), resultUser.getDate());
        assertTrue(resultUser.isStatus());
        assertNull(resultUser.getToken());
    }
    @Test
    public void createUser_userExisting(){
        UserRequestDTO dto = UserRequestDTO.builder().name("john david").surname("doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).build();
        when(userRepo.findByUsername(dto.getUsername())).thenReturn(Optional.empty());
        doNothing().when(validator).validateUsernameIsAvailable(null, dto.getUsername());
        doNothing().when(validator).validateRoleIsNotMaster(dto.getRole());
        doThrow(new UserAlreadyExistsException("Usuario ya registrado")).when(userRepo).findByRut(dto.getRut());

        UserAlreadyExistsException e = assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(dto));

        verify(userRepo, times(1)).findByUsername(dto.getUsername());
        verify(validator, times(1)).validateRoleIsNotMaster(dto.getRole());
        verify(userRepo, times(1)).findByRut(dto.getRut());
        verify(mapper, never()).toEntity(dto);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepo, never()).save(any(User.class));

        assertEquals("Usuario ya registrado", e.getMessage());
    }
    @Test
    public void createUser_usernameIsNotAvailable(){
        UserRequestDTO dto = UserRequestDTO.builder().name("john david").surname("doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).build();
        User user = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(true).build();
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.of(user));
        doThrow(new UsernameAlreadyExistsException("El nombre de usuario ya está en uso")).when(validator)
                .validateUsernameIsAvailable(user, "johnny");

        UsernameAlreadyExistsException e = assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.createUser(dto));

        verify(userRepo, times(1)).findByUsername("johnny");
        verify(validator, times(1)).validateRoleIsNotMaster(dto.getRole());
        verify(userRepo, never()).findByRut(dto.getRut());
        verify(mapper, never()).toEntity(dto);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepo, never()).save(any(User.class));

        assertEquals("El nombre de usuario ya está en uso", e.getMessage());
    }
    @Test
    public void createUser_masterRole(){
        UserRequestDTO dto = UserRequestDTO.builder().name("john david").surname("doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.MASTER).build();
        doThrow(new UserMasterRoleSelectedException("El rol maestro ya ha sido asignado")).when(validator)
                .validateRoleIsNotMaster(dto.getRole());

        UserMasterRoleSelectedException e = assertThrows(UserMasterRoleSelectedException.class,
                () -> userService.createUser(dto));

        verify(validator, times(1)).validateRoleIsNotMaster(dto.getRole());
        verify(userRepo, never()).findByUsername(dto.getUsername());
        verify(userRepo, never()).findByRut(dto.getRut());
        verify(mapper, never()).toEntity(dto);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepo, never()).save(any(User.class));

        assertEquals("El rol maestro ya ha sido asignado", e.getMessage());
    }

    //Prueba para actualizar usuario seleccionado
    @Test
    public void updateUser(){
        UserRequestDTO dto = UserRequestDTO.builder().name("john david").surname("doe").username("johnny")
                .email("test@example.cl").rut("19365567-8").role(Rol.MASTER).build();
        User existingUser = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(true).build();
        when(userRepo.findByUsername(dto.getUsername())).thenReturn(Optional.of(existingUser));
        doNothing().when(validator).validateUsernameIsAvailableForUpdate(existingUser, dto.getRut());
        when(userRepo.findByRut(dto.getRut())).thenReturn(Optional.of(existingUser));
        doNothing().when(validator).validateUserExists(existingUser);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        userService.updateUser(dto);

        verify(userRepo, times(1)).findByUsername(dto.getUsername());
        verify(validator, times(1)).validateUsernameIsAvailableForUpdate(existingUser,
                dto.getRut());
        verify(userRepo, times(1)).findByRut(dto.getRut());
        verify(validator, times(1)).validateUserExists(existingUser);
        verify(userRepo, times(1)).save(userCaptor.capture());

        User updateUser = userCaptor.getValue();
        assertEquals(dto.getUsername(), updateUser.getUsername());
        assertEquals(dto.getEmail(), updateUser.getEmail());
    }
    @Test
    public void updateUser_usernameExisting(){
        UserRequestDTO dto = UserRequestDTO.builder().name("john david").surname("doe").username("johnny")
                .email("test@example.cl").rut("19365567-8").role(Rol.MASTER).build();
        User existingUser = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(true).build();
        when(userRepo.findByRut(dto.getRut())).thenReturn(Optional.of(existingUser));
        doNothing().when(validator).validateUserExists(existingUser);
        when(userRepo.findByUsername(dto.getUsername())).thenReturn(Optional.of(existingUser));
        doThrow(new UsernameAlreadyExistsException("El nombre de usuario ya está en uso por otro usuario"))
                .when(validator).validateUsernameIsAvailableForUpdate(existingUser, dto.getRut());

        UsernameAlreadyExistsException e = assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.updateUser(dto));

        verify(userRepo, times(1)).findByRut(dto.getRut());
        verify(userRepo, times(1)).findByUsername(dto.getUsername());
        verify(validator, times(1)).validateUsernameIsAvailableForUpdate(existingUser,
                dto.getRut());
        verify(validator, times(1)).validateUserExists(existingUser);
        verify(userRepo, never()).save(any(User.class));

        assertEquals("El nombre de usuario ya está en uso por otro usuario", e.getMessage());
    }
    @Test
    public void updateUser_userNotExisting(){
        UserRequestDTO dto = UserRequestDTO.builder().name("john david").surname("doe").username("johnny")
                .email("test@example.cl").rut("19365567-8").role(Rol.MASTER).build();
        when(userRepo.findByRut(dto.getRut())).thenReturn(Optional.empty());
        doThrow(new UserNotFoundException("No existe registro del usuario"))
                .when(validator).validateUserExists(null);

        UserNotFoundException e = assertThrows(UserNotFoundException.class,() -> userService.updateUser(dto));

        verify(userRepo, times(1)).findByRut(dto.getRut());
        verify(validator, times(1)).validateUserExists(null);
        verify(userRepo, never()).findByUsername(dto.getUsername());
        verify(validator, never()).validateUsernameIsAvailableForUpdate(any(), eq(dto.getRut()));
        verify(userRepo, never()).save(any(User.class));

        assertEquals("No existe registro del usuario", e.getMessage());
    }

    //Prueba para actualizar los datos de un usuario
    @Test
    public void updateOwnData(){
        UpdateUserDTO dto = UpdateUserDTO.builder().username("johnnie").email("test@example.com")
                .currentPassword(null).newPassword(null).confirmPassword(null).build();
        User existingUser = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(true).build();
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.of(existingUser));
        doNothing().when(validator).validateIsNotDeleted(existingUser.isStatus());
        when(userRepo.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepo.existsByEmail(dto.getEmail())).thenReturn(false);
        doNothing().when(validator).validateUpdateUser(existingUser, dto, false, false);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        userService.updateOwnData(dto, "johnny");

        verify(userRepo, times(1)).findByUsername("johnny");
        verify(validator, times(1)).validateIsNotDeleted(true);
        verify(userRepo, times(1)).existsByUsername(dto.getUsername());
        verify(userRepo,times(1)).existsByEmail(dto.getEmail());
        verify(validator, times(1)).validateUpdateUser(existingUser, dto, false,
                false);
        verify(userRepo, times(1)).save(userCaptor.capture());

        User updatedUser = userCaptor.getValue();
        assertEquals("johnnie", updatedUser.getUsername());
        assertEquals("test@example.com", updatedUser.getEmail());
    }
    @Test
    public void updateOwnData_changePasswordWithToken(){
        UpdateUserDTO dto = UpdateUserDTO.builder().username("johnnie").email("test@example.com")
                .currentPassword("Test-Token").newPassword("johnny1234").confirmPassword("johnny1234").build();
        User existingUser = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token("Test-Token").status(true).build();
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.of(existingUser));
        doNothing().when(validator).validateIsNotDeleted(existingUser.isStatus());
        when(userRepo.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepo.existsByEmail(dto.getEmail())).thenReturn(false);
        doNothing().when(validator).validateUpdateUser(existingUser, dto, false, false);
        when(passwordEncoder.matches(dto.getCurrentPassword(), existingUser.getPassword())).thenReturn(false);
        when(passwordEncoder.matches(dto.getCurrentPassword(), existingUser.getToken())).thenReturn(true);
        when(passwordEncoder.encode("johnny1234")).thenReturn("encodedPassword");
        doNothing().when(validator).validateCurrentCredentials(false, true);
        doNothing().when(validator).validateNewPasswordMatch(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        userService.updateOwnData(dto, "johnny");

        verify(userRepo, times(1)).findByUsername("johnny");
        verify(validator, times(1)).validateIsNotDeleted(true);
        verify(userRepo, times(1)).existsByUsername(dto.getUsername());
        verify(userRepo,times(1)).existsByEmail(dto.getEmail());
        verify(validator, times(1)).validateUpdateUser(existingUser, dto, false,
                false);
        verify(validator, times(1)).validateCurrentCredentials(false,
                true);
        verify(validator, times(1)).validateNewPasswordMatch(dto);
        verify(passwordEncoder, times(1)).encode("johnny1234");
        verify(userRepo, times(1)).save(userCaptor.capture());

        User updatedUser = userCaptor.getValue();
        assertEquals("johnnie", updatedUser.getUsername());
        assertEquals("test@example.com", updatedUser.getEmail());
        assertEquals("encodedPassword", updatedUser.getPassword());
        assertNull(updatedUser.getToken());
    }
    @Test
    public void updateOwnData_normalChangePassword(){
        UpdateUserDTO dto = UpdateUserDTO.builder().username("johnnie").email("test@example.com")
                .currentPassword("johnny19365").newPassword("johnny1234").confirmPassword("johnny1234").build();
        User existingUser = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(true).build();
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.of(existingUser));
        doNothing().when(validator).validateIsNotDeleted(existingUser.isStatus());
        when(userRepo.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepo.existsByEmail(dto.getEmail())).thenReturn(false);
        doNothing().when(validator).validateUpdateUser(existingUser, dto, false, false);
        when(passwordEncoder.matches(dto.getCurrentPassword(), existingUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("johnny1234")).thenReturn("encodedPassword");
        doNothing().when(validator).validateCurrentCredentials(true, false);
        doNothing().when(validator).validateNewPasswordMatch(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        userService.updateOwnData(dto, "johnny");

        verify(userRepo, times(1)).findByUsername("johnny");
        verify(validator, times(1)).validateIsNotDeleted(true);
        verify(userRepo, times(1)).existsByUsername(dto.getUsername());
        verify(userRepo,times(1)).existsByEmail(dto.getEmail());
        verify(validator, times(1)).validateUpdateUser(existingUser, dto, false,
                false);
        verify(validator, times(1)).validateCurrentCredentials(true,
                false);
        verify(validator, times(1)).validateNewPasswordMatch(dto);
        verify(passwordEncoder, times(1)).encode("johnny1234");
        verify(userRepo, times(1)).save(userCaptor.capture());

        User updatedUser = userCaptor.getValue();
        assertEquals("johnnie", updatedUser.getUsername());
        assertEquals("test@example.com", updatedUser.getEmail());
        assertEquals("encodedPassword", updatedUser.getPassword());
        assertNull(updatedUser.getToken());
    }
    @Test
    public void updateOwnData_invalidCredentials(){
        UpdateUserDTO dto = UpdateUserDTO.builder().username("johnnie").email("test@example.com")
                .currentPassword("johnny19").newPassword("johnny1234").confirmPassword("johnny1234").build();
        User existingUser = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(true).build();
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.of(existingUser));
        doNothing().when(validator).validateIsNotDeleted(existingUser.isStatus());
        when(userRepo.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepo.existsByEmail(dto.getEmail())).thenReturn(false);
        doNothing().when(validator).validateUpdateUser(existingUser, dto, false, false);
        doThrow(new InvalidCredentialsException("La contraseña actual o el código de recuperación son incorrectos"))
                .when(validator).validateCurrentCredentials(false, false);

        InvalidCredentialsException e = assertThrows(InvalidCredentialsException.class,
                () -> userService.updateOwnData(dto, "johnny"));

        verify(userRepo, times(1)).findByUsername("johnny");
        verify(validator, times(1)).validateIsNotDeleted(true);
        verify(userRepo, times(1)).existsByUsername(dto.getUsername());
        verify(userRepo,times(1)).existsByEmail(dto.getEmail());
        verify(validator, times(1)).validateUpdateUser(existingUser, dto, false,
                false);
        verify(validator, times(1)).validateCurrentCredentials(false,
                false);
        verify(validator, never()).validateNewPasswordMatch(dto);
        verify(passwordEncoder, never()).encode("johnny1234");
        verify(userRepo, never()).save(any(User.class));

        assertEquals("La contraseña actual o el código de recuperación son incorrectos", e.getMessage());
    }
    @Test
    public void updateOwnData_passwordNotMatch(){
        UpdateUserDTO dto = UpdateUserDTO.builder().username("johnnie").email("test@example.com")
                .currentPassword("johnny19365").newPassword("johnny5467").confirmPassword("johnny1234").build();
        User existingUser = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(true).build();
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.of(existingUser));
        doNothing().when(validator).validateIsNotDeleted(existingUser.isStatus());
        when(userRepo.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepo.existsByEmail(dto.getEmail())).thenReturn(false);
        doNothing().when(validator).validateUpdateUser(existingUser, dto, false, false);
        when(passwordEncoder.matches(dto.getCurrentPassword(), existingUser.getPassword())).thenReturn(true);
        doNothing().when(validator).validateCurrentCredentials(true, false);
        doThrow(new PasswordMismatchException("Las contraseñas nuevas no coinciden"))
                .when(validator).validateNewPasswordMatch(dto);

        PasswordMismatchException e = assertThrows(PasswordMismatchException.class,
                () -> userService.updateOwnData(dto, "johnny"));

        verify(userRepo, times(1)).findByUsername("johnny");
        verify(validator, times(1)).validateIsNotDeleted(true);
        verify(userRepo, times(1)).existsByUsername(dto.getUsername());
        verify(userRepo,times(1)).existsByEmail(dto.getEmail());
        verify(validator, times(1)).validateUpdateUser(existingUser, dto, false,
                false);
        verify(validator, times(1)).validateCurrentCredentials(true,
                false);
        verify(validator, times(1)).validateNewPasswordMatch(dto);
        verify(passwordEncoder, never()).encode("johnny1234");
        verify(userRepo, never()).save(any(User.class));

        assertEquals("Las contraseñas nuevas no coinciden", e.getMessage());
    }
    @Test
    public void updateUserData_OwnNotFound(){
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.empty());
        UserNotFoundException e = assertThrows(UserNotFoundException.class,() ->
                userService.updateOwnData(any(UpdateUserDTO.class), "johnny"));

        verify(userRepo, times(1)).findByUsername("johnny");
        verify(validator, never()).validateIsNotDeleted(true);
        verify(userRepo, never()).existsByUsername(anyString());
        verify(userRepo, never()).existsByEmail(anyString());
        verify(validator, never()).validateUpdateUser(any(User.class), any(UpdateUserDTO.class), anyBoolean(),
                anyBoolean());
        verify(userRepo, never()).save(any(User.class));

        assertEquals("Usuario no encontrado", e.getMessage());
    }
    @Test
    public void updateUserData_OwnIsDeleted(){
        User existingUser = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(false).build();
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.of(existingUser));
        doThrow(new UserAlreadyDeletedException("Usuario ya está inactivo")).when(validator)
                .validateIsNotDeleted(existingUser.isStatus());
        UserAlreadyDeletedException e = assertThrows(UserAlreadyDeletedException.class,() ->
                userService.updateOwnData(any(UpdateUserDTO.class), "johnny"));

        verify(userRepo, times(1)).findByUsername("johnny");
        verify(validator, times(1)).validateIsNotDeleted(existingUser.isStatus());
        verify(userRepo, never()).existsByUsername(anyString());
        verify(userRepo, never()).existsByEmail(anyString());
        verify(validator, never()).validateUpdateUser(any(User.class), any(UpdateUserDTO.class), anyBoolean(),
                anyBoolean());
        verify(userRepo, never()).save(any(User.class));

        assertEquals("Usuario ya está inactivo", e.getMessage());
    }
    @Test
    public void updateOwnData_usernameExists(){
        UpdateUserDTO dto = UpdateUserDTO.builder().username("johnnie").email("test@example.com")
                .currentPassword(null).newPassword(null).confirmPassword(null).build();
        User existingUser = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(true).build();
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.of(existingUser));
        doNothing().when(validator).validateIsNotDeleted(existingUser.isStatus());
        when(userRepo.existsByUsername(dto.getUsername())).thenReturn(true);
        when(userRepo.existsByEmail(dto.getEmail())).thenReturn(false);
        doThrow(new UsernameAlreadyExistsException("El nombre de usuario ya está en uso")).when(validator)
                .validateUpdateUser(existingUser, dto, true, false);
        UsernameAlreadyExistsException e = assertThrows(UsernameAlreadyExistsException.class,() ->
                userService.updateOwnData(dto, "johnny"));

        verify(userRepo, times(1)).findByUsername("johnny");
        verify(validator, times(1)).validateIsNotDeleted(existingUser.isStatus());
        verify(userRepo, times(1)).existsByUsername(dto.getUsername());
        verify(userRepo, times(1)).existsByEmail(dto.getEmail());
        verify(validator, times(1)).validateUpdateUser(existingUser, dto, true,
                false);
        verify(userRepo, never()).save(any(User.class));

        assertEquals("El nombre de usuario ya está en uso", e.getMessage());
    }
    @Test
    public void updateOwnData_emailExists(){
        UpdateUserDTO dto = UpdateUserDTO.builder().username("johnnie").email("test@example.com")
                .currentPassword(null).newPassword(null).confirmPassword(null).build();
        User existingUser = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(true).build();
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.of(existingUser));
        doNothing().when(validator).validateIsNotDeleted(existingUser.isStatus());
        when(userRepo.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepo.existsByEmail(dto.getEmail())).thenReturn(true);
        doThrow(new EmailAlreadyRegisterException("El email ya está registrado")).when(validator)
                .validateUpdateUser(existingUser, dto, false, true);
        EmailAlreadyRegisterException e = assertThrows(EmailAlreadyRegisterException.class,() ->
                userService.updateOwnData(dto, "johnny"));

        verify(userRepo, times(1)).findByUsername("johnny");
        verify(validator, times(1)).validateIsNotDeleted(existingUser.isStatus());
        verify(userRepo, times(1)).existsByUsername(dto.getUsername());
        verify(userRepo, times(1)).existsByEmail(dto.getEmail());
        verify(validator, times(1)).validateUpdateUser(existingUser, dto, false,
                true);
        verify(userRepo, never()).save(any(User.class));

        assertEquals("El email ya está registrado", e.getMessage());
    }

    //Prueba para eliminar/desactivar un usuario
    @Test
    public void deleteUser(){
        UserRequestDTO dto = UserRequestDTO.builder().name("john david").surname("doe").username("johnny")
                .email("test@example.cl").rut("19365567-8").role(Rol.ADMIN).build();
        User user = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(true).build();
        when(userRepo.findByUsername(dto.getUsername())).thenReturn(Optional.of(user));
        doNothing().when(validator).validateIsNotDeleted(user.isStatus());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        userService.deleteUser(dto);

        verify(userRepo, times(1)).findByUsername(dto.getUsername());
        verify(validator, times(1)).validateIsNotDeleted(true);
        verify(userRepo).save(userCaptor.capture());

        assertFalse(user.isStatus());
    }
    @Test
    public void deleteUser_userAlreadyDeleted(){
        UserRequestDTO dto = UserRequestDTO.builder().name("john david").surname("doe").username("johnny")
                .email("test@example.cl").rut("19365567-8").role(Rol.ADMIN).build();
        User user = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(false).build();
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        doThrow(new UserAlreadyDeletedException("Usuario ya está inactivo")).when(validator)
                .validateIsNotDeleted(user.isStatus());

        UserAlreadyDeletedException e = assertThrows(UserAlreadyDeletedException.class,
                () -> userService.deleteUser(dto));

        verify(userRepo, times(1)).findByUsername(dto.getUsername());
        verify(validator, times(1)).validateIsNotDeleted(user.isStatus());
        verify(userRepo, never()).save(user);
        assertEquals("Usuario ya está inactivo", e.getMessage());
    }

    //Prueba para activar un usuario
    @Test
    public void activateUser(){
        UserRequestDTO dto = UserRequestDTO.builder().name("john david").surname("doe").username("johnny")
                .email("test@example.cl").rut("19365567-8").role(Rol.ADMIN).build();
        User user = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(false).build();
        when(userRepo.findByUsername(dto.getUsername())).thenReturn(Optional.of(user));
        doNothing().when(validator).validateIsActivated(user.isStatus());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        userService.activateUser(dto);

        verify(userRepo, times(1)).findByUsername(dto.getUsername());
        verify(validator, times(1)).validateIsActivated(false);
        verify(userRepo).save(userCaptor.capture());

        assertTrue(user.isStatus());
    }
    @Test
    public void activateUser_validUserActivated(){
        UserRequestDTO dto = UserRequestDTO.builder().name("john david").surname("doe").username("johnny")
                .email("test@example.cl").rut("19365567-8").role(Rol.ADMIN).build();
        User user = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(true).build();
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        doThrow(new UserAlreadyActivatedException("Usuario ya activado")).when(validator)
                .validateIsActivated(user.isStatus());

        UserAlreadyActivatedException e = assertThrows(UserAlreadyActivatedException.class,
                () -> userService.activateUser(dto));

        verify(userRepo, times(1)).findByUsername(dto.getUsername());
        verify(validator, times(1)).validateIsActivated(user.isStatus());
        verify(userRepo, never()).save(user);
        assertEquals("Usuario ya activado", e.getMessage());
    }

    //Prueba para obtener todos los usuarios registrados
    @Test
    public void getAllUsers(){
        User user = User.builder().name("John David").surname("Doe").username("johnny")
                .email("test@test.com").rut("19365567-8").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(true).build();
        User user2 = User.builder().name("Peter").surname("doe").username("Pete1234")
                .email("test@test.com").rut("19365567-0").role(Rol.ADMIN).password("johnny19365").date(LocalDate.now())
                .token(null).status(true).build();
        when(userRepo.findAll()).thenReturn(List.of(user, user2));
        UserResponseDTO dto = UserResponseDTO.builder().name("john david").surname("doe").username("johnny")
                .email("test@example.cl").rut("19365567-8").role(Rol.ADMIN).status(true).date(LocalDate.now()).build();
        UserResponseDTO dto2 = UserResponseDTO.builder().name("Peter").surname("doe").username("Pete1234")
                .email("test@example.cl").rut("19365567-0").role(Rol.ADMIN).status(true).date(LocalDate.now()).build();
        when(mapper.toResponseDTO(user)).thenReturn(dto);
        when(mapper.toResponseDTO(user2)).thenReturn(dto2);

        List<UserResponseDTO> result = userService.getAllUsers();

        verify(userRepo, times(1)).findAll();

        assertEquals(List.of(dto, dto2).size(), result.size());
    }

    //Prueba para obtener la información de un usuario
    @Test
    public void getUserProfile_valid(){
        User user = User.builder().username("johnny").rut("12345678-9").name("John").surname("Doe")
                .email("test@test.com").status(true).date(LocalDate.now()).role(Rol.ADMIN).build();
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserResponseDTO mappedDTO = UserResponseDTO.builder().username("johnny").rut("12345678-9").name("John")
                .surname("Doe").email("test@test.com").status(true).date(LocalDate.now()).role(Rol.ADMIN).build();
        when(mapper.toResponseDTO(user)).thenReturn(mappedDTO);

        UserResponseDTO result = userService.getUserProfile(user.getUsername());

        verify(userRepo, times(1)).findByUsername(user.getUsername());
        verify(mapper, times(1)).toResponseDTO(user);

        assertEquals(mappedDTO.getUsername(), result.getUsername());
        assertEquals(mappedDTO.getRut(), result.getRut());
        assertEquals(mappedDTO.getRole(), result.getRole());
    }

    //Prueba para obtener un usuario por su username
    @Test
    public void getUserByUsername(){
        User user = User.builder().username("johnny").rut("12345678-9").name("John").surname("Doe")
                .email("test@test.com").status(true).date(LocalDate.now()).role(Rol.ADMIN).build();
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername("johnny");

        verify(userRepo, times(1)).findByUsername("johnny");

        assertEquals(user.getUsername(), result.getUsername());
    }
    @Test
    public void getUserWithUsername_userNotFound(){
        when(userRepo.findByUsername("johnny")).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> userService.getUserByUsername("johnny"));

        verify(userRepo, times(1)).findByUsername("johnny");

        assertEquals("Usuario no encontrado", e.getMessage());
    }
}
