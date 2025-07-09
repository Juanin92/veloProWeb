package com.veloproweb.mapper;

import com.veloproweb.model.dto.communication.MessageResponseDTO;
import com.veloproweb.model.entity.user.User;
import com.veloproweb.model.entity.communication.Message;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MessageMapper {

    public MessageResponseDTO toResponse(Message message){
        return MessageResponseDTO.builder()
                .id(message.getId())
                .context(message.getContext())
                .created(message.getCreated())
                .read(message.isRead())
                .delete(message.isDelete())
                .senderName(String.format("%s %s", message.getSenderUser().getName(),
                        message.getSenderUser().getSurname()))
                .build();
    }

    public Message toEntity(String context, User userSender, User receiverUser){
        return Message.builder()
                .context(context)
                .created(LocalDate.now())
                .isRead(false)
                .isDelete(false)
                .senderUser(userSender)
                .receiverUser(receiverUser)
                .build();
    }
}
