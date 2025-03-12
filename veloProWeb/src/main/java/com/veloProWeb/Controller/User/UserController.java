package com.veloProWeb.Controller.User;

import com.veloProWeb.Model.DTO.UpdateUserDTO;
import com.veloProWeb.Model.DTO.UserDTO;
import com.veloProWeb.Model.Entity.User.User;
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

    /**
     * Obtiene una lista de usuarios.
     * @return - R lista de usuarios
     */
    @GetMapping
    public List<User> getListUser(){
        return userService.getAllUser();
    }

    /**
     * Agrega un nuevo usuario
     * @param user - Usuario con los datos que se desea agregar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping("/nuevo-usuario")
    public ResponseEntity<Map<String, String>> addUser(@RequestBody User user){
        Map<String, String> response =  new HashMap<>();
        try{
            userService.addUser(user);
            response.put("message", "Nuevo usuario "+ user.getName() + " " + user.getUsername() + " creado exitosamente");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Actualiza los datos un usuario existente
     * @param user - Usuario con los datos actualizados
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
//    @PutMapping("/editar-usuario")
//    public ResponseEntity<Map<String, String>> updateUser(@RequestBody User user){
//        Map<String, String> response =  new HashMap<>();
//        try{
//            userService.updateUser(user);
//            response.put("message", "Usuario "+ user.getName() + " " + user.getUsername() + " actualizado exitosamente");
//            return ResponseEntity.ok(response);
//        }catch (Exception e){
//            response.put("error", e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//    }

    @PutMapping("/actualizar-usuario")
    public ResponseEntity<Map<String, String>> updateUser(@RequestBody UpdateUserDTO user, @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response =  new HashMap<>();
        try{
            userService.updateUserData(user, userDetails.getUsername());
            response.put("message", "Usuario actualizado exitosamente");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("datos")
    public ResponseEntity<UserDTO> getUserData(@AuthenticationPrincipal UserDetails userDetails){
        try{
            UserDTO dto = userService.getData(userDetails.getUsername());
            return ResponseEntity.ok(dto);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     *Elimina un usuario
     * @param user - Usuario que se desea eliminar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/eliminar-usuario")
    public ResponseEntity<Map<String, String>> deleteUser(@RequestBody User user){
        Map<String, String> response =  new HashMap<>();
        try{
            userService.deleteUser(user);
            response.put("message", "Usuario eliminado exitosamente");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     *Activa un usuario
     * @param user - Usuario que se desea activar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping("/activar-usuario")
    public ResponseEntity<Map<String, String>> activeUser(@RequestBody User user){
        Map<String, String> response =  new HashMap<>();
        try{
            userService.activateUser(user);
            response.put("message", "Usuario activado exitosamente");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
