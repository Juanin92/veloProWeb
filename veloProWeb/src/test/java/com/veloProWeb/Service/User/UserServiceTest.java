package com.veloProWeb.Service.User;

import com.veloProWeb.Model.DTO.UpdateUserDTO;
import com.veloProWeb.Model.DTO.UserDTO;
import com.veloProWeb.Model.Entity.User.User;
import com.veloProWeb.Model.Enum.Rol;
import com.veloProWeb.Repository.UserRepo;
import com.veloProWeb.Validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks private UserService userService;
    @Mock private UserRepo userRepo;
    @Mock private UserValidator validator;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    private User user;
    private UpdateUserDTO updateUserDTO;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setId(1L);
        user.setDate(LocalDate.now());
        user.setName("Juan");
        user.setSurname("Perez");
        user.setUsername("jpp");
        user.setRut("12345678-9");
        user.setEmail("example@gmail.com");
        user.setPassword("jpp12345");
        user.setToken("");
        user.setStatus(true);
        user.setRole(Rol.ADMIN);
    }

    //Prueba para crear usuario
    @Test
    public void addUser_valid(){
        when(userRepo.findByRut(user.getRut())).thenReturn(Optional.empty());
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        userService.addUser(user);
        verify(validator).validate(user);
        verify(userRepo).save(user);
    }
    @Test
    public void addUser_validUsernameExisting(){
        when(userRepo.findByRut(user.getRut())).thenReturn(Optional.empty());
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> userService.addUser(user));
        verify(validator).validate(user);
        verify(userRepo, never()).save(user);
        assertEquals("Este nombre de usuario ya existe", e.getMessage());
    }

    //Prueba para actualizar usuario seleccionado
    @Test
    public void updateUser_valid(){
        assertEquals("Juan", user.getName());
        user.setName("Pedro");
        when(userRepo.findByRut(user.getRut())).thenReturn(Optional.of(user));
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        userService.updateUser(user);
        verify(validator).validate(user);
        verify(userRepo).save(user);
        assertEquals("Pedro", user.getName());
    }
    @Test
    public void updateUser_validUsernameExisting(){
        user.setName("Pedro");
        when(userRepo.findByRut(user.getRut())).thenReturn(Optional.of(user));
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> userService.updateUser(user));

        verify(validator).validate(user);
        verify(userRepo, never()).save(user);
        assertEquals("Este nombre de usuario ya existe", e.getMessage());
    }

    //Prueba para eliminar/desactivar un usuario
    @Test
    public void deleteUser_valid(){
        assertTrue(user.isStatus());
        when(userRepo.findByRut(user.getRut())).thenReturn(Optional.of(user));

        userService.deleteUser(user);
        verify(userRepo).save(user);
        assertFalse(user.isStatus());
    }
    @Test
    public void deleteUser_validUserDeleted(){
        user.setStatus(false);
        when(userRepo.findByRut(user.getRut())).thenReturn(Optional.of(user));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> userService.deleteUser(user));
        verify(userRepo, never()).save(user);
        assertEquals("El usuario ya está inactivo y no puede ser eliminado nuevamente.", e.getMessage());
    }

    //Prueba para activar un usuario
    @Test
    public void activateUser_valid(){
        user.setStatus(false);
        assertFalse(user.isStatus());
        when(userRepo.findByRut(user.getRut())).thenReturn(Optional.of(user));

        userService.activateUser(user);
        verify(userRepo).save(user);
        assertTrue(user.isStatus());
    }
    @Test
    public void activateUser_validUserActivated(){
        when(userRepo.findByRut(user.getRut())).thenReturn(Optional.of(user));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> userService.activateUser(user));
        verify(userRepo, never()).save(user);
        assertEquals("El usuario ya está activo y no puede ser activado nuevamente.", e.getMessage());
    }

    //Prueba para obtener todos los usuarios registrados
    @Test
    public void getAllUser_valid(){
        List<User> list = Collections.singletonList(user);
        when(userRepo.findAll()).thenReturn(list);

        List<UserDTO> result = userService.getAllUser();
        verify(userRepo).findAll();
        assertEquals(list.size(), result.size());
    }

    //Prueba para actualizar los datos de un usuario
    @Test
    public void updateUserData_validUsername(){
        updateUserDTO = new UpdateUserDTO("TestUsername", "example@gmail.com",
                null, null, null);
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepo.existsByUsername(updateUserDTO.getUsername())).thenReturn(false);
        userService.updateUserData(updateUserDTO, user.getUsername());

        verify(userRepo, times(1)).findByUsername("jpp");
        verify(userRepo, times(1)).existsByUsername(updateUserDTO.getUsername());
        verify(userRepo, never()).existsByEmail(updateUserDTO.getEmail());
        verify(validator, times(1)).validateStatus(user.isStatus());
        verify(validator, times(1)).validate(user);
        verify(userRepo, times(1)).save(user);

        assertEquals(updateUserDTO.getUsername(), user.getUsername());
        assertEquals(updateUserDTO.getEmail(), user.getEmail());
    }
    @Test
    public void updateUserData_validExistingUsernameException(){
        updateUserDTO = new UpdateUserDTO("TestUsername", "example@gmail.com",
                null, null, null);
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepo.existsByUsername(updateUserDTO.getUsername())).thenReturn(true);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() ->
                userService.updateUserData(updateUserDTO, user.getUsername()));

        verify(userRepo, times(1)).findByUsername("jpp");
        verify(userRepo, times(1)).existsByUsername(updateUserDTO.getUsername());
        verify(userRepo, never()).existsByEmail(updateUserDTO.getEmail());
        verify(validator, never()).validateStatus(user.isStatus());
        verify(validator, never()).validate(user);
        verify(userRepo, never()).save(user);

        assertEquals(e.getMessage(), "El nombre de usuario ya está en uso");
    }
    @Test
    public void updateUserData_validEmail(){
        updateUserDTO = new UpdateUserDTO("jpp", "new_example@gmail.com",
                null, null, null);
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepo.existsByEmail(updateUserDTO.getEmail())).thenReturn(false);
        userService.updateUserData(updateUserDTO, user.getUsername());

        verify(userRepo, times(1)).findByUsername("jpp");
        verify(userRepo, never()).existsByUsername(updateUserDTO.getUsername());
        verify(userRepo, times(1)).existsByEmail(updateUserDTO.getEmail());
        verify(validator, times(1)).validateStatus(user.isStatus());
        verify(validator, times(1)).validate(user);
        verify(userRepo, times(1)).save(user);

        assertEquals(updateUserDTO.getUsername(), user.getUsername());
        assertEquals(updateUserDTO.getEmail(), user.getEmail());
    }
    @Test
    public void updateUserData_validExistingEmailException(){
        updateUserDTO = new UpdateUserDTO("jpp", "new_example@gmail.com",
                null, null, null);
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(userRepo.existsByEmail(updateUserDTO.getEmail())).thenReturn(true);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() ->
                userService.updateUserData(updateUserDTO, user.getUsername()));

        verify(userRepo, times(1)).findByUsername("jpp");
        verify(userRepo, never()).existsByUsername(updateUserDTO.getUsername());
        verify(userRepo, times(1)).existsByEmail(updateUserDTO.getEmail());
        verify(validator, never()).validateStatus(user.isStatus());
        verify(validator, never()).validate(user);
        verify(userRepo, never()).save(user);

        assertEquals(e.getMessage(), "El email ya está registrado");
    }
    @Test
    public void updateUserData_validPassword() {
        user.setToken(null);
        updateUserDTO = new UpdateUserDTO("jpp", "example@gmail.com",
                "jpp12345", "new12345", "new12345");
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(updateUserDTO.getCurrentPassword(), user.getPassword())).thenReturn(true);
        userService.updateUserData(updateUserDTO, user.getUsername());

        verify(userRepo, times(1)).findByUsername("jpp");
        verify(userRepo, never()).existsByUsername(updateUserDTO.getUsername());
        verify(userRepo, never()).existsByEmail(updateUserDTO.getEmail());
        verify(validator, times(1)).validateStatus(user.isStatus());
        verify(validator, times(1)).validate(user);
        verify(userRepo, times(1)).save(user);

        assertEquals(updateUserDTO.getUsername(), user.getUsername());
        assertEquals(updateUserDTO.getEmail(), user.getEmail());
        assertNull(user.getToken());
    }
    @Test
    public void updateUserData_invalidCurrentPasswordException(){
        updateUserDTO = new UpdateUserDTO("jpp", "example@gmail.com",
                "jpp123", "new12345", "new12345");
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(updateUserDTO.getCurrentPassword(), user.getPassword())).thenReturn(false);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() ->
                userService.updateUserData(updateUserDTO, user.getUsername()));

        verify(userRepo, times(1)).findByUsername("jpp");
        verify(validator, never()).validateStatus(user.isStatus());
        verify(validator, never()).validate(user);
        verify(userRepo, never()).save(user);

        assertEquals(e.getMessage(), "La contraseña actual o el código de recuperación son incorrectos");
    }
    @Test
    public void updateUserData_invalidMatchNewPasswordException(){
        updateUserDTO = new UpdateUserDTO("jpp", "example@gmail.com",
                "jpp12345", "new12345", "new1234");
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(updateUserDTO.getCurrentPassword(), user.getPassword())).thenReturn(true);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() ->
                userService.updateUserData(updateUserDTO, user.getUsername()));

        verify(userRepo, times(1)).findByUsername("jpp");
        verify(validator, never()).validateStatus(user.isStatus());
        verify(validator, never()).validate(user);
        verify(userRepo, never()).save(user);

        assertEquals(e.getMessage(), "Las contraseñas nuevas no coinciden");
    }

    //Prueba para obtener la información de un usuario
    @Test
    public void getData_valid(){
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        UserDTO dto = userService.getData(user.getUsername());

        verify(userRepo, times(1)).findByUsername("jpp");
        assertEquals(dto.getName(), user.getName());
    }
}
