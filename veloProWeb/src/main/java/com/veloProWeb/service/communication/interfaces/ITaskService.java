package com.veloProWeb.service.communication.interfaces;

import com.veloProWeb.model.dto.communication.TaskRequestDTO;
import com.veloProWeb.model.dto.communication.TaskResponseDTO;

import java.util.List;

public interface ITaskService {
    List<TaskResponseDTO> getTaskByUser(String username);
    void createTask(TaskRequestDTO dto);
    void completeTask(Long taskID);
    List<TaskResponseDTO> getAllTasks();
}
