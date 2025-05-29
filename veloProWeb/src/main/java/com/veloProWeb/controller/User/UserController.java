package com.veloProWeb.controller.User;

import com.veloProWeb.model.dto.user.UpdateUserDTO;
import com.veloProWeb.model.dto.user.UserRequestDTO;
import com.veloProWeb.model.dto.user.UserResponseDTO;
import com.veloProWeb.service.reporting.interfaces.IRecordService;
import com.veloProWeb.service.user.interfaces.IUserService;
import com.veloProWeb.util.ResponseMessage;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuario")
@AllArgsConstructor
public class UserController {

    private final IUserService userService;
    private final IRecordService recordService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER' , 'SELLER', 'GUEST', 'WAREHOUSE')")
    public ResponseEntity<List<UserResponseDTO>> getListUser(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/nuevo-usuario")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public ResponseEntity<Map<String, String>> addUser(@RequestBody @Valid UserRequestDTO user,
                                                       @AuthenticationPrincipal UserDetails userDetails){
        userService.createUser(user);
        recordService.registerAction(userDetails, "CREATE",
                String.format("Creó un usuario nuevo: %s", user.getUsername()));
        return new ResponseEntity<>(ResponseMessage.message(
                String.format("Nuevo usuario %s %s creado exitosamente",user.getName() ,user.getUsername())),
                HttpStatus.CREATED);
    }

    @PutMapping("/administrar/editar-usuario")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public ResponseEntity<Map<String, String>> updateUserByAdmin(@RequestBody @Valid UserRequestDTO user,
                                                                 @AuthenticationPrincipal UserDetails userDetails){
        userService.updateUser(user);
        recordService.registerAction(userDetails, "UPDATE",
                String.format("Actualizo los datos: %s", user.getUsername()));
        return new ResponseEntity<>(ResponseMessage.message(
                String.format("Usuario %s %s actualizado exitosamente", user.getName(), user.getUsername())),
                HttpStatus.OK);
    }

    /**
     * Actualiza los datos un usuario existente.
     * @param user - Usuario con los datos actualizados
     * @param userDetails - Detalle del usuario autenticado
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/actualizar-usuario")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER' , 'SELLER', 'GUEST', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> updateUserProfile(@RequestBody UpdateUserDTO user,
                                                                 @AuthenticationPrincipal UserDetails userDetails){
        userService.updateOwnData(user, userDetails.getUsername());
        recordService.registerAction(userDetails, "UPDATE", "Se actualizo sus datos personales");
        return new ResponseEntity<>(ResponseMessage.message("Usuario actualizado exitosamente"), HttpStatus.OK);
    }

    @GetMapping("datos")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER' , 'SELLER', 'GUEST', 'WAREHOUSE')")
    public ResponseEntity<UserResponseDTO> getUserProfile(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(userService.getUserProfile(userDetails.getUsername()));
    }

    @PutMapping("/eliminar-usuario")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public ResponseEntity<Map<String, String>> deleteUser(@RequestBody @Valid UserRequestDTO user,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        userService.deleteUser(user);
        recordService.registerAction(userDetails, "DELETE",
                String.format("Desactivo usuario del sistema: %s", user.getUsername()));
        return new ResponseEntity<>(ResponseMessage.message("Usuario eliminado exitosamente"), HttpStatus.OK);
    }

    @PutMapping("/activar-usuario")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public ResponseEntity<Map<String, String>> activeUser(@RequestBody @Valid UserRequestDTO user,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        userService.activateUser(user);
        recordService.registerAction(userDetails, "ACTIVATE ",
                String.format("activo usuario del sistema: %s", user.getUsername()));
        return new ResponseEntity<>(ResponseMessage.message("Usuario activado exitosamente"), HttpStatus.OK);
    }
}
