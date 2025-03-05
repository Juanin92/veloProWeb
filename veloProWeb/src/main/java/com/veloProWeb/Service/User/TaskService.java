package com.veloProWeb.Service.User;

import com.veloProWeb.Model.DTO.TaskDTO;
import com.veloProWeb.Model.Entity.User.Task;
import com.veloProWeb.Repository.TaskRepo;
import com.veloProWeb.Service.User.Interface.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TaskService implements ITaskService {

    @Autowired private TaskRepo taskRepo;

    /**
     * Obtener una lista de tareas de un usuario.
     * Filtra que el usuario de la tarea sea el mismo usuario seleccionado,
     * y la tarea debe estar true(Activa)
     * @param userID - Identificador del Usuario seleccionado
     * @return - Lista filtrada
     */
    @Override
    public List<Task> getTaskByUser(Long userID) {
        List<Task> tasks = taskRepo.findAll();
        return tasks.stream()
                .filter(task -> Objects.equals(task.getUser().getId(), userID) && task.isStatus())
                .toList();
    }

    /**
     *Crear Tarea para un usuario
     * @param task - Objeto que contiene datos esenciales
     */
    @Override
    public void createTask(Task task) {
        if (task != null && task.getUser() != null) {
            Task newTask = new Task();
            newTask.setId(null);
            newTask.setCreated(LocalDate.now());
            newTask.setUser(task.getUser());
            newTask.setDescription(task.getDescription());
            newTask.setStatus(true);
            taskRepo.save(newTask);
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
                .map(task -> {
                    TaskDTO dto =  new TaskDTO();
                    dto.setCreated(task.getCreated());
                    dto.setDescription(task.getDescription());
                    dto.setStatus(task.isStatus());
                    dto.setUser(String.format("%s %s", task.getUser().getName(), task.getUser().getSurname()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
