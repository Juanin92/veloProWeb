package com.veloproweb.service.communication;

import com.veloproweb.exceptions.communication.TaskNotFoundException;
import com.veloproweb.mapper.TaskMapper;
import com.veloproweb.model.dto.communication.TaskRequestDTO;
import com.veloproweb.model.dto.communication.TaskResponseDTO;
import com.veloproweb.model.entity.communication.Task;
import com.veloproweb.model.entity.user.User;
import com.veloproweb.repository.communication.TaskRepo;
import com.veloproweb.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks private TaskService taskService;
    @Mock private TaskRepo taskRepo;
    @Mock private UserService userService;
    @Mock private TaskMapper mapper;

    //Prueba para obtener una lista de tareas de un usuario
    @Test
    void getTaskByUser(){
        User user = User.builder().id(1L).username("johnny").name("John").surname("Doe").build();
        Task task = Task.builder().id(1L).user(user).status(true).description("Task1").created(LocalDate.now()).build();
        Task task2 = Task.builder().id(2L).user(user).status(true).description("Task").created(LocalDate.now()).build();
        when(taskRepo.findByStatusTrueAndUser(user)).thenReturn(List.of(task, task2));
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        TaskResponseDTO dto = TaskResponseDTO.builder().user("John Doe").status(true).description("Task1").id(1L).build();
        TaskResponseDTO dto2 = TaskResponseDTO.builder().user("John Doe").status(true).description("Task").id(2L).build();
        List<TaskResponseDTO> mappedDtos = List.of(dto, dto2);
        when(mapper.toTaskResponse(task)).thenReturn(dto);
        when(mapper.toTaskResponse(task2)).thenReturn(dto2);

        List<TaskResponseDTO> result = taskService.getTaskByUser("johnny");

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(taskRepo, times(1)).findByStatusTrueAndUser(user);
        verify(mapper, times(2)).toTaskResponse(any(Task.class));

        assertEquals(mappedDtos.size(), result.size());
        assertEquals(mappedDtos.getFirst().getDescription(), result.getFirst().getDescription());
    }

    //Prueba para crear una tarea a un usuario
    @Test
    void createTask(){
        TaskRequestDTO dto = TaskRequestDTO.builder().description("test task").user("johnny").build();
        User user = User.builder().id(1L).username("johnny").name("John").surname("Doe").build();
        when(userService.getUserByUsername(dto.getUser())).thenReturn(user);
        Task mappedTask = Task.builder().created(LocalDate.now()).description("test task").status(true)
                .user(user).build();
        when(mapper.toEntity(dto, user)).thenReturn(mappedTask);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        taskService.createTask(dto);

        verify(userService, times(1)).getUserByUsername(dto.getUser());
        verify(mapper, times(1)).toEntity(dto, user);
        verify(taskRepo, times(1)).save(taskCaptor.capture());

        Task result = taskCaptor.getValue();
        assertTrue(result.isStatus());
        assertEquals(user, result.getUser());
        assertEquals(mappedTask.getDescription(), result.getDescription());
    }

    //Prueba para dejar como completa una tarea
    @Test
    void completeTask(){
        Task task = Task.builder().id(1L).status(true).build();
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        taskService.completeTask(1L);

        verify(taskRepo, times(1)).findById(1L);
        verify(taskRepo, times(1)).save(taskCaptor.capture());

        Task result = taskCaptor.getValue();
        assertFalse(result.isStatus());
    }
    @Test
    void completeTask_taskNotFound(){
        when(taskRepo.findById(6L)).thenReturn(Optional.empty());
        TaskNotFoundException e = assertThrows(TaskNotFoundException.class,() -> taskService.completeTask(6L));

        verify(taskRepo, never()).save(any(Task.class));
        assertEquals("Tarea no encontrada", e.getMessage());
    }

    //Prueba para obtener todas las tareas
    @Test
    void getAllTasks() {
        Task task = Task.builder().id(1L).status(true).description("Task1").created(LocalDate.now()).build();
        Task task2 = Task.builder().id(2L).status(true).description("Task").created(LocalDate.now()).build();
        when(taskRepo.findAll()).thenReturn(List.of(task, task2));

        List<TaskResponseDTO> result = taskService.getAllTasks();

        verify(taskRepo, times(1)).findAll();

        assertEquals(2, result.size());
    }
}
