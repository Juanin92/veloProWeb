package com.veloproweb.service.reporting;

import com.veloproweb.exceptions.user.UserRoleNotFoundException;
import com.veloproweb.model.dto.reporting.RecordResponseDTO;
import com.veloproweb.model.entity.reporting.Record;
import com.veloproweb.model.entity.user.User;
import com.veloproweb.repository.reporting.RecordRepo;
import com.veloproweb.service.user.interfaces.IUserService;
import com.veloproweb.service.reporting.interfaces.IRecordService;
import com.veloproweb.validation.UserValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class RecordService implements IRecordService {

    private final RecordRepo recordRepo;
    private final IUserService userService;
    private final UserValidator validator;

    /**
     * Registrar una entrada al sistema de un usuario.
     * Verifica que el usuario exista por su username y coincida por su rol.
     * @param userDetails - Datos del usuario.
     */
    @Override
    public void registerEntry(UserDetails userDetails) {
        User user = validateAndGetUser(userDetails);

        Record recordEntry = saveRecord(user, "LOGIN", null, LocalDateTime.now(), null,
                null);
        recordRepo.save(recordEntry);
    }

    /**
     * Registrar una salida del sistema de un usuario.
     * Verifica que el usuario exista por su username y coincida por su rol.
     * @param userDetails - Datos del usuario.
     */
    @Override
    public void registerEnd(UserDetails userDetails) {
        User user = validateAndGetUser(userDetails);

        Record recordEnd = saveRecord(user, "LOGOUT", null, null, null,
                LocalDateTime.now());
        recordRepo.save(recordEnd);
    }

    /**
     * Registrar una acción dentro del sistema de un usuario.
     * Verifica que el usuario exista por su username y coincida por su rol.
     * @param userDetails - Datos del usuario.
     * @param action - Acción realizada
     * @param comment - Cadena con un comentario
     */
    @Override
    public void registerAction(UserDetails userDetails, String action, String comment) {
        User user = validateAndGetUser(userDetails);

        Record recordAction = saveRecord(user, action, comment, null, LocalDateTime.now(), null);
        recordRepo.save(recordAction);
    }

    /**
     * Registrar una acción manual de un usuario.
     * @param username - Nombre de usuario que realiza la acción
     * @param action - Acción realizada
     * @param comment - Comentario adicional
     */
    @Override
    public void registerActionManual(String username, String action, String comment) {
        User user = userService.getUserByUsername(username);
        Record recordActionManual = Record.builder()
                .entryDate(null)
                .endDate(null)
                .actionDate(LocalDateTime.now())
                .action(action)
                .comment(comment)
                .user(user)
                .build();
        recordRepo.save(recordActionManual);
    }

    /**
     * Obtener un registro de todos los movimientos o registro del sistema
     *
     * @return - Lista con los registros
     */
    @Override
    public List<RecordResponseDTO> getAllRecord() {
        return recordRepo.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Obtiene un usuario verificando que exista y que tenga el rol correspondiente.
     * @param userDetails - Detalles del usuario autenticado
     * @return - usuario validado
     */
    private User validateAndGetUser(UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        validator.validateUserExists(user);
        validator.validateUserHasRole(user);
        isUserHasRole(user, userDetails);
        return user;
    }

    /**
     * Verifica que el usuario tenga el rol correspondiente a sus permisos.
     * @param user - usuario a verificar
     * @param userDetails - Detalles del usuario autenticado
     */
    private void isUserHasRole(User user, UserDetails userDetails){
        if (userDetails.getAuthorities().stream().noneMatch(grantedAuthority ->
                grantedAuthority.getAuthority().equals(user.getRole().name()))) {
            throw new UserRoleNotFoundException("El rol del usuario no coincide con sus permisos");
        }
    }

    /**
     * Crea un registro de una acción realizada por un usuario.
     * @param user - Usuario que realiza la acción
     * @param action - Acción realizada
     * @param comment - Comentario adicional
     * @param entryDate - Fecha de entrada del registro
     * @param actionDate - Fecha de la acción del registro
     * @param endDate - Fecha de finalización del registro
     */
    private Record saveRecord(User user, String action, String comment,
                            LocalDateTime entryDate, LocalDateTime actionDate, LocalDateTime endDate) {
        return Record.builder()
                .entryDate(entryDate)
                .actionDate(actionDate)
                .endDate(endDate)
                .action(action)
                .comment(comment)
                .user(user)
                .build();
    }

    /**
     * Mapear un registro a un DTO de respuesta
     * @param originalRecord - registro a mapear
     * @return - DTO del registro
     */
    private RecordResponseDTO mapToDTO(Record originalRecord){
        return RecordResponseDTO.builder()
                .entryDate(originalRecord.getEntryDate())
                .actionDate(originalRecord.getActionDate())
                .endDate(originalRecord.getEndDate())
                .action(originalRecord.getAction())
                .comment(originalRecord.getComment())
                .user(String.format("%s %s", originalRecord.getUser().getName(), originalRecord.getUser().getSurname()))
                .build();
    }
}
