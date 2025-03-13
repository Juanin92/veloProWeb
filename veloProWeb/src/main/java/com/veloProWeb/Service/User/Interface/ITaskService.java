package com.veloProWeb.Service.User.Interface;

import com.veloProWeb.Model.DTO.TaskDTO;
import com.veloProWeb.Model.Entity.User.Task;

import java.util.List;

public interface ITaskService {
    List<Task> getTaskByUser(Long userID);
    void createTask(TaskDTO dto);
    void completeTask(Long taskID);
    List<TaskDTO> getAllTasks();
}
