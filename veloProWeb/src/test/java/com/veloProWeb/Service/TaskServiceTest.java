package com.veloProWeb.Service;

import com.veloProWeb.Model.Entity.Task;
import com.veloProWeb.Model.Entity.User;
import com.veloProWeb.Repository.TaskRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks private TaskService taskService;
    @Mock private TaskRepo taskRepo;
    private Task task, task2,task3, task4;
    private User user;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setId(1L);
        task = new Task(1L, "Test 1", true, LocalDate.now(), user);
        task2 = new Task(2L, "Test 1", false, LocalDate.now(), user);
        task3 = new Task(3L, "Test 1", true, LocalDate.now(), user);
        task4 = new Task(3L, "Test 1", true, LocalDate.now(), new User());
    }

    //Prueba para obtener una lista de tarea de un usuario
    @Test
    public void getTaskByUser_valid(){
        List<Task> tasks = Arrays.asList(task,task2,task3,task4);
        List<Task> taskListFiltered = Arrays.asList(task, task3);
        when(taskRepo.findAll()).thenReturn(tasks);
        List<Task> result = taskService.getTaskByUser(user);

        verify(taskRepo, times(1)).findAll();
        assertEquals(2, result.size());
        assertEquals(taskListFiltered, result);
    }

    @Test
    public void createTask_valid(){
        Task newTask = new Task();
        newTask.setUser(user);
        taskService.createTask(newTask);
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepo, times(1)).save(taskArgumentCaptor.capture());
        Task result = taskArgumentCaptor.getValue();
        assertEquals(result.getCreated(), LocalDate.now());
        assertTrue(result.isStatus());
        assertEquals(user, result.getUser());
    }
    @Test
    public void createTask_validNotFoundTaskAndUser(){
        Task newTask = new Task();
        newTask.setUser(null);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> taskService.createTask(newTask));
        verify(taskRepo, never()).save(newTask);
        assertEquals("Tarea debe contener un usuario seleccionado!", e.getMessage());
    }

    //Prueba para crear una tarea a un usuario
    @Test
    public void completeTask_valid(){
        when(taskRepo.findById(1L)).thenReturn(Optional.of(task));
        taskService.completeTask(1L);

        verify(taskRepo).save(task);
        assertFalse(task.isStatus());
    }
    @Test
    public void completeTask_validNotFoundTask(){
        when(taskRepo.findById(6L)).thenReturn(Optional.empty());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> taskService.completeTask(6L));

        verify(taskRepo, never()).save(any(Task.class));
        assertEquals("Tarea no encontrada", e.getMessage());
    }
    @Test
    public void completeTask_validStatusFalse(){
        when(taskRepo.findById(2L)).thenReturn(Optional.of(task2));
        taskService.completeTask(2L);

        verify(taskRepo, never()).save(task);
    }
}
