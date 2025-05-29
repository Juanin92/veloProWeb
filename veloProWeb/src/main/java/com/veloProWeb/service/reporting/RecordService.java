package com.veloProWeb.service.reporting;

import com.veloProWeb.model.entity.reporting.Record;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.repository.reporting.RecordRepo;
import com.veloProWeb.service.user.interfaces.IUserService;
import com.veloProWeb.service.reporting.interfaces.IRecordService;
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

    /**
     * Registrar una entrada al sistema de un usuario.
     * Verifica que el usuario exista por su username y coincida por su rol.
     * @param userDetails - Datos del usuario.
     */
    @Override
    public void registerEntry(UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        if (user != null){
            if (userDetails.getAuthorities().stream().anyMatch(grantedAuthority ->
                    grantedAuthority.getAuthority().equals(user.getRole().name()))){
                Record record = new Record();
                record.setEntryDate(LocalDateTime.now());
                record.setEndaDate(null);
                record.setActionDate(null);
                record.setAction("LOGIN");
                record.setComment(null);
                record.setUser(user);
                recordRepo.save(record);
            }else {
                throw new IllegalArgumentException("Usuario no tiene el rol");
            }
        }else {
            throw new IllegalArgumentException("Usuario no es encontrado");
        }
    }

    /**
     * Registrar una salida del sistema de un usuario.
     * Verifica que el usuario exista por su username y coincida por su rol.
     * @param userDetails - Datos del usuario.
     */
    @Override
    public void registerEnd(UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        if (user != null){
            if (userDetails.getAuthorities().stream().anyMatch(grantedAuthority ->
                    grantedAuthority.getAuthority().equals(user.getRole().name()))){
                Record record = new Record();
                record.setEntryDate(null);
                record.setEndaDate(LocalDateTime.now());
                record.setActionDate(null);
                record.setAction("LOGOUT");
                record.setComment(null);
                record.setUser(user);
                recordRepo.save(record);
            }else {
                throw new IllegalArgumentException("Usuario no tiene el rol");
            }
        }else {
            throw new IllegalArgumentException("Usuario no es encontrado");
        }
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
        User user = userService.getUserByUsername(userDetails.getUsername());
        if (user != null){
            if (userDetails.getAuthorities().stream().anyMatch(grantedAuthority ->
                    grantedAuthority.getAuthority().equals(user.getRole().name()))){
                Record record = new Record();
                record.setEntryDate(null);
                record.setEndaDate(null);
                record.setActionDate(LocalDateTime.now());
                record.setAction(action);
                record.setComment(comment);
                record.setUser(user);
                recordRepo.save(record);
            }else {
                throw new IllegalArgumentException("Usuario no tiene el rol");
            }
        }else {
            throw new IllegalArgumentException("Usuario no es encontrado");
        }
    }

    @Override
    public void registerActionManual(String username, String action, String comment) {
        User user = userService.getUserByUsername(username);
        if (user != null){
            Record record = new Record();
            record.setEntryDate(null);
            record.setEndaDate(null);
            record.setActionDate(LocalDateTime.now());
            record.setAction(action);
            record.setComment(comment);
            record.setUser(user);
            recordRepo.save(record);
        }else {
            Record record = new Record();
            record.setEntryDate(null);
            record.setEndaDate(null);
            record.setActionDate(LocalDateTime.now());
            record.setAction(action);
            record.setComment("PELIGRO" + comment);
            record.setUser(null);
            recordRepo.save(record);
        }
    }

    /**
     * Obtener un registro de todos los movimientos o registro del sistema
     * @return - Lista con los registros
     */
    @Override
    public List<Record> getAllRecord() {
        return recordRepo.findAll();
    }
}
