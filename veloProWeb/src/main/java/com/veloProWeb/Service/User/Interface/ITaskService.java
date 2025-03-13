package com.veloProWeb.Service.User.Interface;

import com.veloProWeb.Model.DTO.TaskDTO;

import java.util.List;

public interface ITaskService {
    List<TaskDTO> getTaskByUser(String username);
    void createTask(TaskDTO dto);
    void completeTask(Long taskID);
    List<TaskDTO> getAllTasks();
}
