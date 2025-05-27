package com.veloProWeb.service.User;

import com.veloProWeb.model.dto.TaskDTO;
import com.veloProWeb.model.entity.User.Task;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.repository.TaskRepo;
import com.veloProWeb.service.User.Interface.ITaskService;
import com.veloProWeb.service.User.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TaskService implements ITaskService {

    @Autowired private TaskRepo taskRepo;
    @Autowired private IUserService userService;

    /**
     * Obtener una lista de tareas de un usuario.
     * Filtra que el usuario de la tarea sea el mismo usuario seleccionado,
     * y la tarea debe estar true(Activa)
     * @param username - Nombre de Usuario del usuario seleccionado
     * @return - Lista filtrada
     */
    @Override
    public List<TaskDTO> getTaskByUser(String username) {
        User user = userService.getUserByUsername(username);
        List<Task> tasks = taskRepo.findAll();
        return tasks.stream()
                .filter(task -> task.isStatus() && Objects.equals(task.getUser().getId(), user.getId()))
                .map(this::mapTaskToTaskDTO)
                .collect(Collectors.toList());
    }

    /**
     *Crear Tarea para un usuario
     * @param dto - Objeto que contiene datos necesarios
     */
    @Override
    public void createTask(TaskDTO dto) {
        if (dto != null && !dto.getUser().trim().isEmpty()) {
            Task task = new Task();
            task.setId(null);
            task.setCreated(LocalDate.now());
            task.setDescription(dto.getDescription());
            task.setStatus(true);
            User user = userService.getUserByUsername(dto.getUser());
            task.setUser(user);
            taskRepo.save(task);
        }else {
            throw new IllegalArgumentException("Tarea debe contener un usuario seleccionado!");
        }
    }

    /**
     * Completar una tarea seleccionada
     * @param taskID - Identificador de la tarea
     */
    @Override
    public void completeTask(Long taskID) {
        Task task = taskRepo.findById(taskID).orElse(null);
        if (task != null) {
            if (task.isStatus()){
                task.setStatus(false);
                taskRepo.save(task);
            }
        }else {
            throw new IllegalArgumentException("Tarea no encontrada");
        }
    }

    /**
     * Obtener una lista de todas las tareas registradas
     * @return - Lista de las tareas registradas con cierta información
     */
    @Override
    public List<TaskDTO> getAllTasks() {
        List<Task> taskList = taskRepo.findAll();
        return taskList.stream()
                .map(this::mapTaskToTaskDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte un task a un taskDTO
     * @param task - Objeto a convertir
     * @return - Objeto dto
     */
    private TaskDTO mapTaskToTaskDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setCreated(task.getCreated());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.isStatus());
        dto.setUser(String.format("%s %s", task.getUser().getName(), task.getUser().getSurname()));
        return dto;
    }
}
