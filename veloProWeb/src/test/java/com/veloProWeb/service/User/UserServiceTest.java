package com.veloProWeb.service.User;

import com.veloProWeb.model.dto.UpdateUserDTO;
import com.veloProWeb.model.dto.UserDTO;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.model.Enum.Rol;
import com.veloProWeb.repository.UserRepo;
import com.veloProWeb.security.Service.CodeGenerator;
import com.veloProWeb.util.EmailService;
import com.veloProWeb.validation.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
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
    @Mock private UserDetails userDetails;
    @Mock private CodeGenerator codeGenerator;
    @Mock private EmailService emailService;
    private User user;
    private UpdateUserDTO updateUserDTO;
    private UserDTO userDTO;

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

        userDTO = new UserDTO("Carlos", "Perez", "cape", "12345678-0",
                "test@gmail.com",Rol.SELLER, true, LocalDate.now());
    }

    //Prueba para crear usuario
    @Test
    public void addUser_valid(){
        when(userRepo.findByRut(userDTO.getRut())).thenReturn(Optional.empty());
        when(userRepo.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());

        userService.addUser(userDTO);
        verify(userRepo).findByUsername(userDTO.getUsername());
        verify(userRepo).findByRut(userDTO.getRut());
        verify(validator).validate(any(User.class));
        verify(userRepo).save(any(User.class));
    }
    @Test
    public void addUser_validUsernameExisting(){
        userDTO.setUsername("jpp");
        when(userRepo.findByRut(userDTO.getRut())).thenReturn(Optional.empty());
        when(userRepo.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(user));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> userService.addUser(userDTO));
        verify(validator, never()).validate(any(User.class));
        verify(userRepo, never()).save(any(User.class));
        assertEquals("Este nombre de usuario ya existe", e.getMessage());
    }

    //Prueba para actualizar usuario seleccionado
    @Test
    public void updateUser_valid(){
        assertEquals("Juan", user.getName());
        user.setName("Pedro");
        when(userRepo.findByRut(user.getRut())).thenReturn(Optional.of(user));
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        userService.updateUser(userDTO);
        verify(validator).validate(user);
        verify(userRepo).save(user);
        assertEquals("Pedro", user.getName());
    }
    @Test
    public void updateUser_validUsernameExisting(){
        user.setName("Pedro");
        when(userRepo.findByRut(user.getRut())).thenReturn(Optional.of(user));
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> userService.updateUser(userDTO));

        verify(validator).validate(user);
        verify(userRepo, never()).save(user);
        assertEquals("Este nombre de usuario ya existe", e.getMessage());
    }

    //Prueba para eliminar/desactivar un usuario
    @Test
    public void deleteUser_valid(){
        assertTrue(user.isStatus());
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        userService.deleteUser(user.getUsername());
        verify(userRepo).findByUsername(user.getUsername());
        verify(userRepo).save(user);
        assertFalse(user.isStatus());
    }
    @Test
    public void deleteUser_validUserDeleted(){
        user.setStatus(false);
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> userService.deleteUser(user.getUsername()));
        verify(userRepo, never()).save(user);
        assertEquals("El usuario ya está inactivo y no puede ser eliminado nuevamente.", e.getMessage());
    }

    //Prueba para activar un usuario
    @Test
    public void activateUser_valid(){
        user.setStatus(false);
        assertFalse(user.isStatus());
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        userService.activateUser(user.getUsername());
        verify(userRepo).findByUsername(user.getUsername());
        verify(userRepo).save(user);
        assertTrue(user.isStatus());
    }
    @Test
    public void activateUser_validUserActivated(){
        when(userRepo.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> userService.activateUser(user.getUsername()));
        verify(userRepo).findByUsername(user.getUsername());
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

    //Prueba para obtener la autorización de un usuario
    @Test
    public void getAuthUser_valid(){
        String password = "jpp12345";
        when(userDetails.getPassword()).thenReturn(user.getPassword());
        when(passwordEncoder.matches(password, userDetails.getPassword())).thenReturn(true);
        boolean result = userService.getAuthUser(password, userDetails);
        verify(passwordEncoder, times(1)).matches(password, userDetails.getPassword());
        assertTrue(result);
    }
    @Test
    public void getAuthUser_invalid(){
        String password = "jpp12";
        when(userDetails.getPassword()).thenReturn(user.getPassword());
        when(passwordEncoder.matches(password, userDetails.getPassword())).thenReturn(false);
        boolean result = userService.getAuthUser(password, userDetails);
        verify(passwordEncoder, times(1)).matches(password, userDetails.getPassword());
        assertFalse(result);
    }

    //Prueba para enviar código de seguridad generado al correo del usuario
    @Test
    public void sendEmailCode_valid(){
        String code = "exampleCodeGenerator";
        String encryptedCode = "EncryptedCodeGenerator";
        when(codeGenerator.generate()).thenReturn(code);
        when(passwordEncoder.encode(code)).thenReturn(encryptedCode);
        userService.sendEmailCode(user.getUsername());

        verify(codeGenerator, times(1)).generate();
        verify(emailService, times(1)).sendPasswordResetCode(user, code);
        verify(passwordEncoder, times(1)).encode(code);
        verify(userRepo, times(1)).save(user);

        assertEquals(user.getToken(), encryptedCode);
    }

    //Prueba para obtener una autenticación de un usuario mediante un código de seguridad
    @Test
    public void getAuthUserToken_valid(){
        String code = "exampleCodeGenerator";
        user.setToken("exampleCodeGenerator");
        when(userRepo.findByUsername("jpp")).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);
        when(passwordEncoder.matches(code, user.getToken())).thenReturn(true);
        userService.getAuthUserToken("jpp", code);

        verify(userRepo, times(1)).findByUsername("jpp");
        verify(passwordEncoder, times(1)).matches(code, code);
        verify(userRepo, times(1)).save(user);

        assertNull(user.getToken());
    }
    @Test
    public void getAuthUserToken_validNotMatchesCode(){
        String code = "CodeGenerator";
        user.setToken("exampleCodeGenerator");
        when(userRepo.findByUsername("jpp")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(code, user.getToken())).thenReturn(false);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() ->
                userService.getAuthUserToken(user.getUsername(), code));

        verify(userRepo, times(1)).findByUsername("jpp");
        verify(passwordEncoder, times(1)).matches(code, user.getToken());
        verify(userRepo, never()).save(user);

        assertEquals("Los códigos de seguridad no tiene similitud", e.getMessage());
    }
}
