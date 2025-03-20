package com.veloProWeb.Service.User;

import com.veloProWeb.Model.DTO.UpdateUserDTO;
import com.veloProWeb.Model.DTO.UserDTO;
import com.veloProWeb.Model.Entity.User.User;
import com.veloProWeb.Repository.UserRepo;
import com.veloProWeb.Security.CodeGenerator;
import com.veloProWeb.Service.User.Interface.IUserService;
import com.veloProWeb.Utils.EmailService;
import com.veloProWeb.Validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {

    @Autowired private UserRepo userRepository;
    @Autowired private UserValidator validator;
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private CodeGenerator codeGenerator;
    @Autowired private EmailService emailService;

    /**
     * Crea un usuario nuevo.
     * Válida que no exista un registro del usuario y que el nombre de username no este tampoco registrado
     * @param dto - Objeto con los datos necesario de usuario
     */
    @Override
    public void addUser(UserDTO dto) {
        User userDB = getUserCreated(dto.getRut());
        if (userDB != null){
            throw new IllegalArgumentException("Usuario Existente: Ya hay existe el usuario");
        } else {
            User user = new User();
            user.setId(null);
            user.setDate(LocalDate.now());
            user.setName(dto.getName().substring(0, 1).toUpperCase() + dto.getName().substring(1));
            user.setSurname(dto.getSurname().substring(0, 1).toUpperCase() + dto.getSurname().substring(1));
            user.setRut(dto.getRut());
            user.setEmail(dto.getEmail());
            user.setRole(dto.getRole());
            user.setToken(null);
            user.setStatus(true);
            Optional<User> userNameExist = userRepository.findByUsername(dto.getUsername());
            if (userNameExist.isPresent() && userNameExist.get().getUsername().equalsIgnoreCase(dto.getUsername())) {
                throw new IllegalArgumentException("Este nombre de usuario ya existe");
            }else {
                user.setUsername(dto.getUsername());
            }
            user.setPassword(user.getUsername() + user.getRut().substring(0,5));
            validator.validate(user);
            user.setPassword(passwordEncoder.encode(user.getUsername() + user.getRut().substring(0,5)));
            userRepository.save(user);
        }
    }

    /**
     * Actualizar los datos de un usuario seleccionado.
     * Válida que el username no esté registrado anteriormente y el usuario exista
     * @param dto - Objeto con los datos actualizados del usuario
     */
    @Override
    public void updateUser(UserDTO dto) {
        User existingUser = getUserCreated(dto.getRut());
        if (existingUser == null){
            throw new IllegalArgumentException("El usuario no existe en la base de datos.");
        }
        existingUser.setName(dto.getName().substring(0, 1).toUpperCase() + dto.getName().substring(1));
        existingUser.setSurname(dto.getSurname().substring(0, 1).toUpperCase() + dto.getSurname().substring(1));
        existingUser.setRut(dto.getRut());
        Optional<User> userNameExist = userRepository.findByUsername(dto.getUsername());
        if (userNameExist.isPresent() && !userNameExist.get().getId().equals(existingUser.getId())) {
            throw new IllegalArgumentException("Este nombre de usuario ya existe");
        }else {
            existingUser.setUsername(dto.getUsername());
        }
        existingUser.setEmail(dto.getEmail());
        existingUser.setToken(null);
        validator.validate(existingUser);
        existingUser.setPassword(passwordEncoder.encode(existingUser.getPassword()));
        userRepository.save(existingUser);
    }

    /**
     * Eliminar/Desactivar usuario.
     * Válida que el usuario exista y no esté ya eliminado
     * @param username - Cadena con el nombre de usuario
     */
    @Override
    public void deleteUser(String username) {
        User user = getUserWithUsername(username);
        if (user.isStatus()) {
            user.setStatus(false);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("El usuario ya está inactivo y no puede ser eliminado nuevamente.");
        }
    }

    /**
     * Activar un usuario.
     * Válida que usuario exista y esté eliminado
     * @param username - Cadena con el nombre de usuario
     */
    @Override
    public void activateUser(String username) {
        User user = getUserWithUsername(username);
        if (!user.isStatus()) {
            user.setStatus(true);
            user.setDate(LocalDate.now());
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("El usuario ya está activo y no puede ser activado nuevamente.");
        }
    }

    /**
     * Obtiene una lista de todos los usuarios registrados
     * @return - Lista con los usuarios
     */
    @Override
    public List<UserDTO> getAllUser() {
        return userRepository.findAll().stream().map(user -> {
            return new UserDTO(user.getName(), user.getSurname(), user.getUsername(), user.getRut(),
                    user.getEmail(), user.getRole(), user.isStatus(), user.getDate());
        }).collect(Collectors.toList());
    }

    /**
     * Obtiene un usuario ya registrado por su username.
     * @param username - El nombre de usuario a buscar.
     * @return - usuario con el mismo rut o null si no se encuentra
     */
    @Override
    public User getUserWithUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser.orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    /**
     * Verifica si la contraseña seleccionada coincide con la contraseña del usuario autenticado
     * @param password - Contraseña entregada por el usuario
     * @param userDetails - Usuario autenticado
     * @return - True si hay coincidencia, False cuando no hay coincidencia
     */
    @Override
    public boolean getAuthUser(String password, UserDetails userDetails) {
        return passwordEncoder.matches(password, userDetails.getPassword());
    }

    /**
     * Verifica que el código de seguridad coincide con el usuario autenticado.
     * @param username - nombre de usuario autenticado
     * @param token - código de seguridad
     */
    @Override
    public void getAuthUserToken(String username, String token) {
        User user = getUserWithUsername(username);
        if (user != null && user.getToken() != null) {
            if (passwordEncoder.matches(token, user.getToken())){
                user.setToken(null);
                userRepository.save(user);
            }else {
                throw new IllegalArgumentException("Los códigos de seguridad no tiene similitud");
            }
        }
    }

    /**
     *  Envia un correo con el código de seguridad generado aleatoriamente.
     *  Encripta el código al guardarlo
     * @param user - Usuario al que se le envia el correo
     */
    @Override
    public void sendEmailCode(User user) {
        String code = codeGenerator.generate();
        emailService.sendPasswordResetCode(user, code);
        user.setToken(passwordEncoder.encode(code));
        userRepository.save(user);
    }

    @Override
    public void sendEmailUpdatePassword(User user) {

    }

    /**
     * Actualiza los datos de un usuario existente.
     * Permite modificar el nombre de usuario, el correo electrónico y la contraseña de un usuario.
     * Verifica que nombre de usuario y email nuevo no estén en uso.
     * Verifica que la contraseña actual o el código de recuperación sean correctos.
     * @param dto - Clase con los datos necesarios a modificar
     * @param username - Nombre del usuario a modificar
     */
    @Override
    public void updateUserData(UpdateUserDTO dto, String username) {
        User user = getUserWithUsername(username);
        if (!user.getUsername().equals(dto.getUsername()) && existByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }
        if (!user.getEmail().equals(dto.getEmail()) && existByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (dto.getCurrentPassword() != null && !dto.getCurrentPassword().isEmpty()) {
            boolean isCurrentPasswordValid = passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword());
            boolean isTokenValid = user.getToken() != null && dto.getCurrentPassword().equals(user.getToken());
            if (!isCurrentPasswordValid && !isTokenValid) {
                throw new IllegalArgumentException("La contraseña actual o el código de recuperación son incorrectos");
            }
            if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
                throw new IllegalArgumentException("Las contraseñas nuevas no coinciden");
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            user.setToken(null);
        }

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        validator.validateStatus(user.isStatus());
        validator.validate(user);
        userRepository.save(user);
    }

    /**
     * Obtiene los datos de un usuario existente
     * @param username - Nombre de usuario a buscar
     * @return - Clase DTO con los datos necesarios.
     */
    @Override
    public UserDTO getData(String username) {
        User user = getUserWithUsername(username);
        return new UserDTO(user.getName(), user.getSurname(), user.getUsername(), user.getRut(),
                user.getEmail(), user.getRole(), user.isStatus(), user.getDate());
    }

    @Override
    public boolean hasRequiredRole(UserDetails userDetails, String... roles) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> java.util.Arrays.asList(roles).contains(authority));
    }

    /**
     * Obtiene un usuario ya registrado por su RUT.
     * @param rut - El RUT del usuario a buscar.
     * @return - usuario con el mismo RUT o null si no se encuentra
     */
    private User getUserCreated(String rut){
        Optional<User> user = userRepository.findByRut(rut);
        return user.orElse(null);
    }

    /**
     * Verifica si existe un registro de nombre de usuario similar.
     * @param username - cadena de nombre de usuario
     * @return - Devuelve Verdad o Falso si existe en el registro
     */
    private boolean existByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    /**
     * Verifica si existe un registro de un email similar.
     * @param email - cadena de email de usuario
     * @return - Devuelve Verdad o Falso si existe en el registro
     */
    private boolean existByEmail(String email){
        return userRepository.existsByEmail(email);
    }
}
