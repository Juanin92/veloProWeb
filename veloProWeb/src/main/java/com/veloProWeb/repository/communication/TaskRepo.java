package com.veloProWeb.repository.communication;

import com.veloProWeb.model.entity.communication.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
}
