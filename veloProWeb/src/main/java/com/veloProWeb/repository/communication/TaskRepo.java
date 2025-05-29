package com.veloProWeb.repository.communication;

import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.model.entity.communication.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    List<Task> findByStatusTrueAndUser(User user);
}
