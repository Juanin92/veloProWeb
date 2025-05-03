package com.veloProWeb.service.User.Interface;

import com.veloProWeb.model.dto.General.MessageDTO;
import com.veloProWeb.model.entity.User.Message;

import java.util.List;

public interface IMessageService {
    List<Message> getAllMessages();
    List<MessageDTO> getMessageByUser(String username);
    void isReadMessage(Long id, String context);
    void isDeleteMessage(Long id, String context);
    void sendMessage(MessageDTO dto, String senderUsername);
}
