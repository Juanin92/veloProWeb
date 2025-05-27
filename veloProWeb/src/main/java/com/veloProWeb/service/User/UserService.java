package com.veloProWeb.service.User;

import com.veloProWeb.exceptions.user.UserNotFoundException;
import com.veloProWeb.mapper.UserMapper;
import com.veloProWeb.model.dto.user.UpdateUserDTO;
import com.veloProWeb.model.dto.user.UserRequestDTO;
import com.veloProWeb.model.dto.user.UserResponseDTO;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.repository.UserRepo;
import com.veloProWeb.service.User.Interface.IUserService;
import com.veloProWeb.validation.UserValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements IUserService {

    private final UserRepo userRepository;
    private final UserValidator validator;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    /**
     * Crea un usuario nuevo.
     * Válida que no exista un registro del usuario y que el nombre de username no este tampoco registrado
     * @param dto - Objeto con los datos necesario de usuario
     */
    @Transactional
    @Override
    public void createUser(UserRequestDTO dto) {
        validator.validateRoleIsNotMaster(dto.getRole());
        validateUsernameIsAvailable(dto.getUsername());
        User existingUser = findUserByRut(dto.getRut());
        validator.validateUserDoesNotExist(existingUser);

        User user = mapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    /**
     * Actualizar los datos de un usuario seleccionado. Actualizado por un administrador.
     * Válida que el username no esté registrado anteriormente y el usuario exista
     * @param dto - Objeto con los datos actualizados del usuario
     */
    @Transactional
    @Override
    public void updateUser(UserRequestDTO dto) {
        User user = findUserByRut(dto.getRut());
        validator.validateUserExists(user);
        validateUsernameIsAvailableForUpdate(dto.getUsername(), dto.getRut());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        userRepository.save(user);
    }

    /**
     * Esta función es cuando un usuario actualiza sus propios datos.
     * Actualiza los datos de un usuario existente, incluyendo la posibilidad de cambiar su contraseña.
     * @param dto - Objeto con los datos actualizados del usuario
     * @param username - Nombre de usuario del usuario que está actualizando sus datos
     */
    @Transactional
    @Override
    public void updateOwnData(UpdateUserDTO dto, String username) {
        User user = getUserByUsername(username);
        validator.validateIsNotDeleted(user.isStatus());
        validator.validateUpdateUser(user, dto, existByUsername(dto.getUsername()), existByEmail(dto.getEmail()));

        changePasswordWithCurrentCredentials(dto, user);

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        userRepository.save(user);
    }

    /**
     * Eliminar/Desactivar usuario.
     * Válida que el usuario exista y no esté ya eliminado
     * @param dto - Objeto con los datos del usuario
     */
    @Transactional
    @Override
    public void deleteUser(UserRequestDTO dto) {
        User user = getUserByUsername(dto.getUsername());
        validator.validateIsNotDeleted(user.isStatus());
        user.setStatus(false);
        userRepository.save(user);
    }

    /**
     * Activar un usuario.
     * Válida que usuario exista y esté eliminado
     * @param dto - Objeto con los datos del usuario
     */
    @Transactional
    @Override
    public void activateUser(UserRequestDTO dto) {
        User user = getUserByUsername(dto.getUsername());
        validator.validateIsActivated(user.isStatus());
        user.setStatus(true);
        user.setDate(LocalDate.now());
        userRepository.save(user);
    }

    /**
     * Obtiene una lista de todos los usuarios registrados
     * @return - Lista de usuarios registrados
     */
    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    /**
     * Obtiene los datos de un usuario existente
     * @param username - Nombre de usuario a buscar
     * @return - Clase DTO con los datos necesarios.
     */
    @Override
    public UserResponseDTO getUserProfile(String username) {
        User user = getUserByUsername(username);
        return mapper.toResponseDTO(user);
    }

    /**
     * Obtiene un usuario ya registrado por su username.
     * @param username - El nombre de usuario a buscar.
     * @return - usuario con el mismo rut o null si no se encuentra
     */
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
    }

    /**
     * Cambia la contraseña del usuario con las credenciales actuales.
     * Valida que la contraseña actual sea correcta y que la nueva contraseña coincida.
     * @param dto - Objeto con los datos de actualización del usuario
     * @param user - Usuario al que se le cambiará la contraseña
     */
    private void  changePasswordWithCurrentCredentials(UpdateUserDTO dto, User user){
        if (dto.getCurrentPassword() != null && !dto.getCurrentPassword().isEmpty()) {
            validator.validatePassword(dto);
            boolean isCurrentPasswordValid = passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword());
            boolean isTokenValid = user.getToken() != null && passwordEncoder.matches(dto.getCurrentPassword(),
                    user.getToken());
            validator.validateCurrentCredentials(isCurrentPasswordValid, isTokenValid);
            validator.validateNewPasswordMatch(dto);
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            user.setToken(null);
        }
    }

    /**
     * Valida que el nombre de usuario no esté registrado.
     * @param username - nombre de usuario a validar
     */
    private void validateUsernameIsAvailable(String username){
        User user = userRepository.findByUsername(username).orElse(null);
        validator.validateUsernameIsAvailable(user, username);
    }

    /**
     * Valida que el nombre de usuario no esté registrado al actualizar.
     * @param newUsername - nuevo nombre de usuario a validar
     * @param rutOriginal - RUT original del usuario
     */
    private void validateUsernameIsAvailableForUpdate(String newUsername, String rutOriginal) {
        User user = userRepository.findByUsername(newUsername).orElse(null);
        validator.validateUsernameIsAvailableForUpdate(user, rutOriginal);
    }


    /**
     * Obtiene un usuario ya registrado por su RUT.
     * @param rut - El RUT del usuario a buscar.
     * @return - usuario con el mismo RUT o null si no se encuentra
     */
    private User findUserByRut(String rut){
        return userRepository.findByRut(rut).orElse(null);
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
