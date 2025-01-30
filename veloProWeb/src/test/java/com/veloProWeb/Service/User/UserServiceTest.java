package com.veloProWeb.Service.User;

import com.veloProWeb.Model.Entity.User;
import com.veloProWeb.Model.Enum.Rol;
import com.veloProWeb.Repository.UserRepo;
import com.veloProWeb.Validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private User user;

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

        List<User> result = userService.getAllUser();
        verify(userRepo).findAll();
        assertEquals(list, result);
    }
}
