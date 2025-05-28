package com.veloProWeb.service.communication.interfaces;

import com.veloProWeb.model.dto.communication.MessageRequestDTO;
import com.veloProWeb.model.dto.communication.MessageResponseDTO;

import java.util.List;

public interface IMessageService {
    List<MessageResponseDTO> getMessageByUser(String username);
    void markMessageAsRead(Long id, String username);
    void markMessageAsDeleted(Long id, String username);
    void sendMessage(MessageRequestDTO dto, String senderUsername);
}
