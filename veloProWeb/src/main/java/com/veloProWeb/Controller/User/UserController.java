package com.veloProWeb.Controller.User;

import com.veloProWeb.Model.DTO.AuthRequestDTO;
import com.veloProWeb.Model.DTO.UpdateUserDTO;
import com.veloProWeb.Model.DTO.UserDTO;
import com.veloProWeb.Service.Record.IRecordService;
import com.veloProWeb.Service.User.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired private IUserService userService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene una lista de usuarios.
     * Verifica que solo los roles admin y master puede obtener la lista.
     * @param userDetails - Usuario autenticado.
     * @return - Lista con los datos de usuarios registrados
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getListUser(@AuthenticationPrincipal UserDetails userDetails){
        if (userService.hasRequiredRole(userDetails, "ADMIN", "MASTER", "WAREHOUSE", "SELLER", "GUEST")){
            return ResponseEntity.ok(userService.getAllUser());
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Agrega un nuevo usuario
     * @param user - Usuario con los datos que se desea agregar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping("/nuevo-usuario")
    public ResponseEntity<Map<String, String>> addUser(@RequestBody UserDTO user, @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response =  new HashMap<>();
        try{
            if (userService.hasRequiredRole(userDetails, "ADMIN", "MASTER")){
                userService.addUser(user);
                recordService.registerAction(userDetails, "CREATE", "Creó un usuario nuevo: " + user.getUsername());
                response.put("message", "Nuevo usuario "+ user.getName() + " " + user.getUsername() + " creado exitosamente");
                return ResponseEntity.ok(response);
            }else{
                recordService.registerAction(userDetails, "CREATE_FAILURE",
                        "Error: " + userDetails.getUsername() + " ingreso indebido");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }catch (Exception e){
            response.put("error", e.getMessage());
            recordService.registerAction(userDetails, "CREATE_FAILURE",
                    "ERROR: crear usuario(" + user.getUsername() + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Actualiza los datos un usuario existente
     * @param user - Usuario con los datos actualizados
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/administrar/editar-usuario")
    public ResponseEntity<Map<String, String>> updateUserByAdmin(@RequestBody UserDTO user,
                                                                 @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response =  new HashMap<>();
        try{
            if (userService.hasRequiredRole(userDetails, "ADMIN", "MASTER")){
                userService.updateUser(user);
                recordService.registerAction(userDetails, "UPDATE", "Actualizo los datos: " + user.getUsername());
                response.put("message", "Usuario "+ user.getName() + " " + user.getUsername() + " actualizado exitosamente");
                return ResponseEntity.ok(response);
            }else{
                recordService.registerAction(userDetails, "UPDATE_FAILURE",
                        "Error: " + userDetails.getUsername() + " ingreso indebido");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }catch (Exception e){
            response.put("error", e.getMessage());
            recordService.registerAction(userDetails, "UPDATE_FAILURE",
                    "ERROR: actualizar usuario(" + user.getUsername() + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Actualiza los datos un usuario existente.
     * @param user - Usuario con los datos actualizados
     * @param userDetails - Detalle del usuario autenticado
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/actualizar-usuario")
    public ResponseEntity<Map<String, String>> updateUserProfile(@RequestBody UpdateUserDTO user, @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response =  new HashMap<>();
        try{
            userService.updateUserData(user, userDetails.getUsername());
            recordService.registerAction(userDetails, "UPDATE", "Se actualizo sus datos personales");
            response.put("message", "Usuario actualizado exitosamente");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            recordService.registerAction(userDetails, "UPDATE_FAILURE", "Error al actualizar datos personales: " + e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Obtiene datos de un usuario existente
     * @param userDetails - Detalle del usuario ya autenticado
     * @return - Los datos necesario del usuario a compartir
     */
    @GetMapping("datos")
    public ResponseEntity<UserDTO> getUserProfile(@AuthenticationPrincipal UserDetails userDetails){
        try{
            UserDTO dto = userService.getData(userDetails.getUsername());
            return ResponseEntity.ok(dto);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     *Elimina un usuario
     * @param  auth - datos necesarios para eliminar un usuario
     * @param  userDetails - Detalle del usuario autenticado
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/eliminar-usuario")
    public ResponseEntity<Map<String, String>> deleteUser(@RequestBody AuthRequestDTO auth,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response =  new HashMap<>();
        try{
            if (userService.hasRequiredRole(userDetails, "ADMIN", "MASTER")){
                if (userService.getAuthUser(auth.getToken(), userDetails)){
                    userService.deleteUser(auth.getIdentifier());
                    recordService.registerAction(userDetails, "DELETE",
                            "Desactivo usuario del sistema: " + auth.getIdentifier());
                    response.put("message", "Usuario eliminado exitosamente");
                    return ResponseEntity.ok(response);
                }else{
                    response.put("error", "No autorizado");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            }else{
                recordService.registerAction(userDetails, "LIST_USER_FAILURE",
                        "Error: " + userDetails.getUsername() + " ingreso indebido");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }catch (Exception e){
            response.put("error", e.getMessage());
            recordService.registerAction(userDetails, "DELETE_FAILURE",
                    "ERROR: desactivar usuario(" + auth.getIdentifier() + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     *Activa un usuario
     * @param  auth - datos necesarios para activar un usuario
     * @param  userDetails - Detalle del usuario autenticado
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/activar-usuario")
    public ResponseEntity<Map<String, String>> activeUser(@RequestBody AuthRequestDTO auth,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response =  new HashMap<>();
        try{
            if (userService.hasRequiredRole(userDetails, "ADMIN", "MASTER")){
                if (userService.getAuthUser(auth.getToken(), userDetails)){
                    userService.activateUser(auth.getIdentifier());
                    recordService.registerAction(userDetails, "ACTIVATE ",
                            "activo usuario del sistema: " + auth.getIdentifier());
                    response.put("message", "Usuario activado exitosamente");
                    return ResponseEntity.ok(response);
                }else{
                    response.put("error", "No autorizado");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            }else{
                recordService.registerAction(userDetails, "LIST_USER_FAILURE",
                        "Error: " + userDetails.getUsername() + " ingreso indebido");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }catch (Exception e){
            response.put("error", e.getMessage());
            recordService.registerAction(userDetails, "ACTIVATE_FAILURE",
                    "ERROR: activar usuario(" + auth.getIdentifier() + "): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
