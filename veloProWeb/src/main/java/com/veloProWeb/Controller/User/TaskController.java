package com.veloProWeb.Controller.User;

import com.veloProWeb.Model.Entity.User.Task;
import com.veloProWeb.Service.User.Interface.ITaskService;
import com.veloProWeb.Service.User.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping()
    public ResponseEntity<List<Task>> getTasks(@RequestParam Long userId){
        try{
            return ResponseEntity.ok(taskService.getTaskByUser(userId));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createTask(@RequestBody Task task){
        HashMap<String, String> response = new HashMap<>();
        try{
            response.put("message", "Tarea asignada correctamente");
            taskService.createTask(task);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> completeTask(@RequestParam Long taskID){
        HashMap<String, String> response = new HashMap<>();
        try{
            response.put("message", "Tarea Completada");
            taskService.completeTask(taskID);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            response.put("Error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
