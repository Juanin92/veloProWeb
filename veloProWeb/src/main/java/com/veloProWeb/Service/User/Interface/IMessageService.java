package com.veloProWeb.Service.User.Interface;

import com.veloProWeb.Model.Entity.User.Message;

import java.util.List;

public interface IMessageService {
    List<Message> getAllMessages();
    List<Message> getMessageByUser(Long userID);
    void isReadMessage(Long id, String context);
    void isDeleteMessage(Long id, String context);
    void sendMessage(Message message);
}
