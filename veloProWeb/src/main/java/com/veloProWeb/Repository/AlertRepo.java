package com.veloProWeb.Repository;

import com.veloProWeb.Model.Entity.User.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepo extends JpaRepository<Alert, Long> {
}
