package com.veloProWeb.service.communication.interfaces;

import com.veloProWeb.model.dto.TaskDTO;

import java.util.List;

public interface ITaskService {
    List<TaskDTO> getTaskByUser(String username);
    void createTask(TaskDTO dto);
    void completeTask(Long taskID);
    List<TaskDTO> getAllTasks();
}
