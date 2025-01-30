package com.veloProWeb.Controller;

import com.veloProWeb.Model.Entity.User;
import com.veloProWeb.Service.User.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar operaciones relacionadas con usuarios.
 * Este controlador proporciona endpoints para listar, agregar, actualizar, eliminar y activar usuarios.
 */
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
    @PostMapping("/editar-usuario")
    public ResponseEntity<Map<String, String>> updateUser(@RequestBody User user){
        Map<String, String> response =  new HashMap<>();
        try{
            userService.updateUser(user);
            response.put("message", "Usuario "+ user.getName() + " " + user.getUsername() + " actualizado exitosamente");
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     *Elimina un usuario
     * @param user - Usuario que se desea eliminar
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping("/eliminar-usuario")
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
    @PostMapping("/activar-usuario")
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
