package com.veloProWeb.Repository;

import com.veloProWeb.Model.Entity.User.Message;
import com.veloProWeb.Model.Entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findByReceiverUser(User user);
}
