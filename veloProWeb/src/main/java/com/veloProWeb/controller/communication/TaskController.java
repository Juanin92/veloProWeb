package com.veloProWeb.controller.communication;

import com.veloProWeb.model.dto.communication.TaskRequestDTO;
import com.veloProWeb.model.dto.communication.TaskResponseDTO;
import com.veloProWeb.service.reporting.interfaces.IRecordService;
import com.veloProWeb.service.communication.interfaces.ITaskService;
import com.veloProWeb.service.user.Interface.IUserService;
import com.veloProWeb.util.ResponseMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tareas")
@AllArgsConstructor
@Validated
public class TaskController {

    private final ITaskService taskService;
    private final IUserService userService;
    private final IRecordService recordService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER' , 'SELLER', 'GUEST', 'WAREHOUSE')")
    public ResponseEntity<List<TaskResponseDTO>> getTasks(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(taskService.getTaskByUser(userDetails.getUsername()));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public ResponseEntity<Map<String, String>> createTask(@RequestBody @Valid TaskRequestDTO task,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        taskService.createTask(task);
        recordService.registerAction(userDetails, "TASK", "Creación de tarea para " + task.getUser());
        return ResponseEntity.ok(ResponseMessage.message("Tarea asignada correctamente"));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER' , 'SELLER', 'GUEST', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> completeTask(@RequestParam
                                                                @NotNull(message = "ID de la tarea es obligatorio")
                                                                Long taskID,
                                                            @AuthenticationPrincipal UserDetails userDetails){
        taskService.completeTask(taskID);
        recordService.registerAction(userDetails, "TASK", "Tarea completada");
        return ResponseEntity.ok(ResponseMessage.message("Tarea Completada"));
    }

    @GetMapping("lista-tarea")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(){
        return ResponseEntity.ok(taskService.getAllTasks());
    }
}
