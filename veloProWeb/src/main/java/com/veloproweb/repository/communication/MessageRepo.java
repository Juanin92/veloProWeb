package com.veloproweb.repository.communication;

import com.veloproweb.model.entity.communication.Message;
import com.veloproweb.model.entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findByReceiverUserAndIsDeleteFalse(User user);
}
