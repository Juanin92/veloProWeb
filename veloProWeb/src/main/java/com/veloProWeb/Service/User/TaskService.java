package com.veloProWeb.Service.User;

import com.veloProWeb.Model.Entity.User.Task;
import com.veloProWeb.Model.Entity.User.User;
import com.veloProWeb.Repository.TaskRepo;
import com.veloProWeb.Service.User.Interface.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService implements ITaskService {

    @Autowired private TaskRepo taskRepo;

    /**
     * Obtener una lista de tareas de un usuario.
     * Filtra que el usuario de la tarea sea el mismo usuario seleccionado,
     * y la tarea debe estar true(Activa)
     * @param user - Usuario seleccionado
     * @return - Lista filtrada
     */
    @Override
    public List<Task> getTaskByUser(User user) {
        List<Task> tasks = taskRepo.findAll();
        return tasks.stream()
                .filter(task -> task.getUser().equals(user) && task.isStatus())
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
}
