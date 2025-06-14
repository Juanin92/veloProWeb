package com.veloproweb.service.user;

import com.veloproweb.exceptions.user.InvalidTokenException;
import com.veloproweb.model.entity.User.User;
import com.veloproweb.repository.UserRepo;
import com.veloproweb.security.service.CodeGenerator;
import com.veloproweb.service.user.interfaces.ILoginService;
import com.veloproweb.service.user.interfaces.IUserService;
import com.veloproweb.util.EmailService;
import com.veloproweb.validation.UserValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class LoginService implements ILoginService {

    private final UserRepo userRepo;
    private final IUserService userService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CodeGenerator codeGenerator;
    private final UserValidator validator;

    /**
     * Verifica si la contraseña seleccionada coincide con la contraseña del usuario autenticado
     * @param password - Contraseña entregada por el usuario
     * @param userDetails - Usuario autenticado
     * @return - True si hay coincidencia, False cuando no hay coincidencia
     */
    @Override
    public boolean isPasswordValid(String password, UserDetails userDetails) {
        return passwordEncoder.matches(password, userDetails.getPassword());
    }

    /**
     * Verifica que el código de seguridad coincide con el usuario autenticado.
     * @param username - nombre de usuario autenticado
     * @param token - código de seguridad
     */
    @Override
    public void validateSecurityToken(String username, String token) {
        User user = userService.getUserByUsername(username);
        if (user != null && user.getToken() != null) {
            if (!passwordEncoder.matches(token, user.getToken())){
                throw new InvalidTokenException("El código de seguridad ingresado es incorrecto");
            }
        }
    }

    /**
     *  Envia un correo con el código de seguridad generado aleatoriamente.
     *  Encripta el código al guardarlo
     * @param username - Usuario al que se le envia el correo
     */
    @Transactional
    @Override
    public void sendSecurityTokenByEmail(String username) {
        User user = userService.getUserByUsername(username);
        validator.validateUserIsNotDeleted(user.isStatus());
        String code = codeGenerator.generate();
        emailService.sendPasswordResetCode(user, code);
        user.setToken(passwordEncoder.encode(code));
        userRepo.save(user);
    }

    /**
     * Verifica que el usuario autenticado no se encuentre eliminado.
     * @param username - Nombre de usuario autenticado
     */
    @Override
    public void validateLoginAccess(String username) {
        User user = userService.getUserByUsername(username);
        validator.validateUserIsNotDeleted(user.isStatus());
    }
}
