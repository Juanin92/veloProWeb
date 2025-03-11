package com.veloProWeb.Service.User;

import com.veloProWeb.Model.DTO.UpdateUserDTO;
import com.veloProWeb.Model.Entity.User.User;
import com.veloProWeb.Repository.UserRepo;
import com.veloProWeb.Service.User.Interface.IUserService;
import com.veloProWeb.Validation.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired private UserRepo userRepository;
    @Autowired private UserValidator validator;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    /**
     * Crea un usuario nuevo.
     * Válida que no exista un registro del usuario y que el nombre de username no este tampoco registrado
     * @param user - Objeto con los datos necesario de usuario
     */
    @Override
    public void addUser(User user) {
        User userDB = getUserCreated(user.getRut());
        if (userDB != null){
            throw new IllegalArgumentException("Usuario Existente: Ya hay existe el usuario");
        } else {
            validator.validate(user);
            user.setId(null);
            user.setStatus(true);
            user.setDate(LocalDate.now());
            user.setName(user.getName().substring(0, 1).toUpperCase() + user.getName().substring(1));
            user.setSurname(user.getSurname().substring(0, 1).toUpperCase() + user.getSurname().substring(1));
            User userNameExist = getUserWithUsername(user.getUsername());
            if (userNameExist != null && userNameExist.getUsername().equalsIgnoreCase(user.getUsername())) {
                throw new IllegalArgumentException("Este nombre de usuario ya existe");
            }else {
                user.setUsername(user.getUsername());
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        }
    }

    /**
     * Actualizar los datos de un usuario seleccionado.
     * Válida que el username no esté registrado anteriormente y el usuario exista
     * @param user - Objeto con los datos actualizados del usuario
     */
    @Override
    public void updateUser(User user) {
        User existingUser = getUserCreated(user.getRut());
        if (existingUser == null){
            throw new IllegalArgumentException("El usuario no existe en la base de datos.");
        }
        validator.validate(user);
        user.setName(user.getName().substring(0, 1).toUpperCase() + user.getName().substring(1));
        user.setSurname(user.getSurname().substring(0, 1).toUpperCase() + user.getSurname().substring(1));
        user.setRut(user.getRut());
        User userNameExist = getUserWithUsername(user.getUsername());
        if (userNameExist != null && userNameExist.getUsername().equalsIgnoreCase(user.getUsername())) {
            throw new IllegalArgumentException("Este nombre de usuario ya existe");
        }else {
            user.setUsername(user.getUsername());
        }
        user.setEmail(user.getEmail());
        user.setToken(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * Eliminar/Desactivar usuario.
     * Válida que el usuario exista y no esté ya eliminado
     * @param user - Objeto con los datos de usuario
     */
    @Override
    public void deleteUser(User user) {
        User existingUser = getUserCreated(user.getRut());
        if (existingUser == null){
            throw new IllegalArgumentException("El usuario no existe en la base de datos.");
        }
        if (existingUser.isStatus()) {
            existingUser.setStatus(false);
            userRepository.save(existingUser);
        } else {
            throw new IllegalArgumentException("El usuario ya está inactivo y no puede ser eliminado nuevamente.");
        }
    }

    /**
     * Activar un usuario.
     * Válida que usuario exista y esté eliminado
     * @param user - Objeto con los datos del usuario
     */
    @Override
    public void activateUser(User user) {
        User existingUser = getUserCreated(user.getRut());
        if (existingUser == null){
            throw new IllegalArgumentException("El usuario no existe en la base de datos.");
        }
        if (!existingUser.isStatus()) {
            existingUser.setStatus(true);
            existingUser.setDate(LocalDate.now());
            userRepository.save(existingUser);
        } else {
            throw new IllegalArgumentException("El usuario ya está activo y no puede ser activado nuevamente.");
        }
    }

    /**
     * Obtiene una lista de todos los usuarios registrados
     * @return - Lista con los usuarios
     */
    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
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

    @Override
    public User getAuthUser(String username, String pass) {
        return null;
    }

    @Override
    public User getAuthUserToken(String username, String token) {
        return null;
    }

    @Override
    public void sendEmailCode(User user) {

    }

    @Override
    public void sendEmailUpdatePassword(User user) {

    }

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
