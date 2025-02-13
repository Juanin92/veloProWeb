package com.veloProWeb.Service.User.Interface;

import com.veloProWeb.Model.Entity.User.Task;
import com.veloProWeb.Model.Entity.User.User;

import java.util.List;

public interface ITaskService {
    List<Task> getTaskByUser(Long userID);
    void createTask(Task task);
    void completeTask(Long taskID);
}
