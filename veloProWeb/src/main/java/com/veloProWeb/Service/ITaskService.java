package com.veloProWeb.Service;

import com.veloProWeb.Model.Entity.Task;
import com.veloProWeb.Model.Entity.User;

import java.util.List;

public interface ITaskService {
    List<Task> getTaskByUser(User user);
    void createTask(Task task);
    void completeTask(Long taskID);
}
