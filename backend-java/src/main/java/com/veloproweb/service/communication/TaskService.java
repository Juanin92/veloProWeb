package com.veloproweb.service.communication;

import com.veloproweb.exceptions.communication.TaskNotFoundException;
import com.veloproweb.mapper.TaskMapper;
import com.veloproweb.model.dto.communication.TaskRequestDTO;
import com.veloproweb.model.dto.communication.TaskResponseDTO;
import com.veloproweb.model.entity.communication.Task;
import com.veloproweb.model.entity.user.User;
import com.veloproweb.repository.communication.TaskRepo;
import com.veloproweb.service.communication.interfaces.ITaskService;
import com.veloproweb.service.user.interfaces.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService implements ITaskService {

    private final TaskRepo taskRepo;
    private final IUserService userService;
    private final TaskMapper mapper;

    /**
     * Obtener una lista de tareas de un usuario.
     * Filtra que el usuario de la tarea sea el mismo usuario seleccionado,
     * y la tarea debe estar true(Activa)
     * @param username - Nombre de Usuario del usuario seleccionado
     * @return - Lista filtrada
     */
    @Override
    public List<TaskResponseDTO> getTaskByUser(String username) {
        User user = userService.getUserByUsername(username);
        List<Task> tasks = taskRepo.findByStatusTrueAndUser(user);
        return tasks.stream()
                .map(mapper::toTaskResponse)
                .toList();
    }

    /**
     *Crear Tarea para un usuario
     * @param dto - Objeto que contiene datos necesarios
     */
    @Transactional
    @Override
    public void createTask(TaskRequestDTO dto) {
        User user = userService.getUserByUsername(dto.getUser());
        Task task = mapper.toEntity(dto, user);
        taskRepo.save(task);
    }

    /**
     * Completar una tarea seleccionada
     * @param taskID - Identificador de la tarea
     */
    @Transactional
    @Override
    public void completeTask(Long taskID) {
        Task task = taskRepo.findById(taskID).orElseThrow(() -> new TaskNotFoundException("Tarea no encontrada"));
        if (task.isStatus()){
            task.setStatus(false);
            taskRepo.save(task);
        }
    }

    /**
     * Obtener una lista de todas las tareas registradas
     * @return - Lista de las tareas registradas con cierta información
     */
    @Override
    public List<TaskResponseDTO> getAllTasks() {
        List<Task> taskList = taskRepo.findAll();
        return taskList.stream()
                .map(mapper::toTaskResponse)
                .toList();
    }
}
