package com.veloProWeb.Validation;

import com.veloProWeb.Model.Entity.User;
import com.veloProWeb.Model.Enum.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @InjectMocks private UserValidator validator = new UserValidator();
    private User user;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setId(1L);
        user.setName("Juan");
        user.setSurname("Perez");
        user.setDate(LocalDate.now());
        user.setStatus(true);
        user.setEmail("example@gmail.com");
        user.setRole(Rol.ADMIN);
        user.setRut("12345678-9");
        user.setPassword("pass1234");
        user.setUsername("JP2");
    }

    //Prueba para validar los datos de los usuarios
    @Test
    public void validate_valid(){
        validator.validate(user);
    }

    //Prueba para validar el estado del usuario
    @Test
    public void validateStatus_valid(){
        validator.validateStatus(user.isStatus());
        assertTrue(user.isStatus());
    }
    @Test
    public void validateStatus_validException(){
        user.setStatus(false);
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class, () -> validator.validateStatus(user.isStatus()));
        assertEquals("Usuario ha sido desactivado", exception.getMessage());
    }

    //Prueba para validar el Rol del usuario
    @Test
    public void validateRole_valid(){
        user.setRole(Rol.MASTER);
        validator.validate(user);
    }
    @Test
    public void validateRole_validException(){
        user.setRole(null);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Seleccione un rol para el usuario", e.getMessage());
    }

    //Prueba validar el rut del usuario
    @ParameterizedTest
    @ValueSource(strings = {"12345678-9", "9876543-2", "12345678-K", "9876543-K", "12345678-k"})
    public void validateRut_valid(String rut){
        user.setRut(rut);
        validator.validate(user);
    }
    @Test
    public void validateRut_validBlank(){
        user.setRut(" ");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("El rut no es correcto.", e.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"123443-9", "2132322", "prueba"})
    public void validateRut_validNotMatch(String rut){
        user.setRut(rut);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("El rut no es correcto.", e.getMessage());
    }

    //Prueba validar el nombre del usuario
    @Test
    public void validateName_valid(){
        validator.validate(user);
    }
    @Test
    public void validateName_validBlank(){
        user.setName(" ");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese nombre válido.", e.getMessage());
    }
    @Test
    public void validateName_validNull(){
        user.setName(null);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese nombre válido.", e.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"ju", "pe", "a"})
    public void validateName_validShortLength(String name){
        user.setName(name);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese nombre válido.", e.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"juan123", "123pedro"})
    public void validateName_validNoMatch(String name){
        user.setName(name);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese nombre válido.", e.getMessage());
    }

    //Prueba validar el apellido del usuario
    @Test
    public void validateSurname_valid(){
        validator.validate(user);
    }
    @Test
    public void validateSurname_validBlank(){
        user.setSurname(" ");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese apellido válido.", e.getMessage());
    }
    @Test
    public void validateSurname_validNull(){
        user.setSurname(null);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese apellido válido.", e.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"p", "pe", "a"})
    public void validateSurname_validShortLength(String name){
        user.setSurname(name);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese apellido válido.", e.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"perez123", "123perez"})
    public void validateSurname_validNoMatch(String name){
        user.setSurname(name);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese apellido válido.", e.getMessage());
    }

    //Prueba validar email del usuario
    @Test
    public void validateEmail_valid(){
        validator.validate(user);
    }
    @Test
    public void validateEmail_validBlank(){
        user.setEmail(" ");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese Email válido.", e.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"prueba", "prueba@correo", "pruebacorreo.cl"})
    public void validateEmail_validNoMatch(String email){
        user.setEmail(email);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese Email válido.", e.getMessage());
    }

    //Prueba validar el nombre de usuario del usuario
    @Test
    public void validateUsername_valid(){
        validator.validate(user);
    }
    @Test
    public void validateUsername_validBlank(){
        user.setUsername(" ");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese nombre de usuario.", e.getMessage());
    }
    @Test
    public void validateUsername_validNull(){
        user.setUsername(null);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese nombre de usuario.", e.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"p", "pe", "a"})
    public void validateUsername_validShortLength(String name){
        user.setUsername(name);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese nombre de usuario.", e.getMessage());
    }

    //Prueba validar contraseña del usuario
    @Test
    public void validatePassword_valid(){
        validator.validate(user);
    }
    @Test
    public void validatePassword_validBlank(){
        user.setPassword(" ");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese contraseña válido. (Debe tener 8 o más caracteres o números)", e.getMessage());
    }
    @Test
    public void validatePassword_validNull(){
        user.setPassword(null);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese contraseña válido. (Debe tener 8 o más caracteres o números)", e.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"pass123", "1234", "a"})
    public void validatePassword_validShortLength(String password){
        user.setPassword(password);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> validator.validate(user));
        assertEquals("Ingrese contraseña válido. (Debe tener 8 o más caracteres o números)", e.getMessage());
    }
}
