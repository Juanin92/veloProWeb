package com.veloproweb.mapper;

import com.veloproweb.model.dto.communication.TaskRequestDTO;
import com.veloproweb.model.dto.communication.TaskResponseDTO;
import com.veloproweb.model.entity.User.User;
import com.veloproweb.model.entity.communication.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequestDTO dto, User user){
        return Task.builder()
                .description(dto.getDescription())
                .status(true)
                .created(LocalDate.now())
                .user(user)
                .build();
    }

    public TaskResponseDTO toTaskResponse(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .created(task.getCreated())
                .description(task.getDescription())
                .status(task.isStatus())
                .user(String.format("%s %s", task.getUser().getName(), task.getUser().getSurname()))
                .build();
    }
}
