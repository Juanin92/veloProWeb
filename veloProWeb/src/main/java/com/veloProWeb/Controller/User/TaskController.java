package com.veloProWeb.Controller.User;

import com.veloProWeb.Model.DTO.TaskDTO;
import com.veloProWeb.Service.Record.IRecordService;
import com.veloProWeb.Service.User.Interface.ITaskService;
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
@RequestMapping("/tareas")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    @Autowired private ITaskService taskService;
    @Autowired private IUserService userService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene tareas asignadas de un usuario
     * @param userDetails - usuario autenticado
     * @return - ResponseEntity con una lista de tareas
     */
    @GetMapping()
    public ResponseEntity<List<TaskDTO>> getTasks(@AuthenticationPrincipal UserDetails userDetails){
        try{
            recordService.registerAction(userDetails, "TASK", "Tareas vista");
            return ResponseEntity.ok(taskService.getTaskByUser(userDetails.getUsername()));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Crea tareas asignadas
     * @param task - Objeto con los datos necesarios para crear la tarea
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createTask(@RequestBody TaskDTO task,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        HashMap<String, String> response = new HashMap<>();
        try{
            taskService.createTask(task);
            recordService.registerAction(userDetails, "TASK", "Creación de tarea para " + task.getUser());
            response.put("message", "Tarea asignada correctamente");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("Error", e.getMessage());
            recordService.registerAction(userDetails, "TASK_FAILURE", "ERROR: crear tarea : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Deja como completada una tarea
     * @param taskID - Identificador de la tarea seleccionada
     * @return - ResponseEntity con un mensaje de éxito o error según sea el caso
     */
    @PutMapping
    public ResponseEntity<Map<String, String>> completeTask(@RequestParam Long taskID,
                                                            @AuthenticationPrincipal UserDetails userDetails){
        HashMap<String, String> response = new HashMap<>();
        try{
            taskService.completeTask(taskID);
            recordService.registerAction(userDetails, "TASK", "Tarea completada");
            response.put("message", "Tarea Completada");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("Error", e.getMessage());
            recordService.registerAction(userDetails, "TASK_FAILURE", "ERROR: Completar tarea : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Obtiene todas las tareas asignadas
     * @return - ResponseEntity con una lista de tareas
     */
    @GetMapping("lista-tarea")
    public ResponseEntity<List<TaskDTO>> getAllTasks(){
        try{
            return ResponseEntity.ok(taskService.getAllTasks());
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
