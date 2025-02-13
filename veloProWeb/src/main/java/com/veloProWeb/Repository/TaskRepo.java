package com.veloProWeb.Repository;

import com.veloProWeb.Model.Entity.User.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
}
