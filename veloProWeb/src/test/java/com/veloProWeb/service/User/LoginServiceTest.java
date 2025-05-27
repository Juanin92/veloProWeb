package com.veloProWeb.service.User;

import com.veloProWeb.exceptions.user.InvalidTokenException;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.repository.UserRepo;
import com.veloProWeb.security.Service.CodeGenerator;
import com.veloProWeb.service.User.Interface.IUserService;
import com.veloProWeb.util.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @InjectMocks private LoginService loginService;
    @Mock private UserRepo userRepo;
    @Mock private IUserService userService;
    @Mock private EmailService emailService;
    @Spy private BCryptPasswordEncoder passwordEncoder;
    @Mock private CodeGenerator codeGenerator;
    @Mock private UserDetails userDetails;

    //Prueba para verificar que la contraseña almacenada y la contraseña ingresada coinciden
    @Test
    void isPasswordValid() {
        when(passwordEncoder.matches("testPassword", userDetails.getPassword())).thenReturn(true);
        boolean result = loginService.isPasswordValid("testPassword", userDetails);
        assertTrue(result);
    }

    //Prueba para verificar que el token almacenado y el token ingresado coinciden
    @Test
    void validateSecurityToken() {
        String originalToken = "testToken";
        User user = User.builder().username("johnny").name("John").surname("Doe").token("testToken").build();
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        when(passwordEncoder.matches(originalToken, user.getToken())).thenReturn(true);

        loginService.validateSecurityToken("johnny", originalToken);

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(passwordEncoder, times(1)).matches(originalToken, user.getToken());
    }
    @Test
    void validateSecurityToken_exception() {
        String originalToken = "testToken";
        User user = User.builder().username("johnny").name("John").surname("Doe").token("token").build();
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        when(passwordEncoder.matches(originalToken, user.getToken())).thenReturn(false);

        InvalidTokenException e = assertThrows(InvalidTokenException.class,
                () -> loginService.validateSecurityToken("johnny", originalToken));

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(passwordEncoder, times(1)).matches(originalToken, user.getToken());

        assertEquals("Los códigos de seguridad no tiene similitud", e.getMessage());
    }

    //Prueba para enviar un correo con el código de seguridad generado aleatoriamente
    @Test
    void sendSecurityTokenByEmail() {
        String code = "testCode";
        User user = User.builder().username("johnny").name("John").surname("Doe").token(null).build();
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        when(codeGenerator.generate()).thenReturn(code);
        doNothing().when(emailService).sendPasswordResetCode(user, code);
        when(passwordEncoder.encode("testCode")).thenReturn("encodedTestCode");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        loginService.sendSecurityTokenByEmail("johnny");

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(codeGenerator, times(1)).generate();
        verify(emailService, times(1)).sendPasswordResetCode(user, code);
        verify(userRepo, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("encodedTestCode", savedUser.getToken());
    }
}