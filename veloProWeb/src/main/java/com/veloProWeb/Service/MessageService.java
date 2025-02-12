package com.veloProWeb.Service;

import com.veloProWeb.Model.Entity.Message;
import com.veloProWeb.Model.Entity.User;
import com.veloProWeb.Repository.MessageRepo;
import com.veloProWeb.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MessageService implements IMessageService{

    @Autowired private MessageRepo messageRepo;
    @Autowired private UserRepo userRepo;

    @Override
    public List<Message> getAllMessages() {
        return messageRepo.findAll();
    }

    @Override
    public List<Message> getMessageByUser(Long userID) {
        User user = userRepo.findById(userID).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return messageRepo.findByUserID(user.getId());
    }


    @Override
    public void isReadMessage(Long id, String context) {
        Message message = getMessageById(id);
        if (message != null){
            if (message.getContext().equals(context)){
                message.setRead(true);
                messageRepo.save(message);
            }else{
                throw new IllegalArgumentException("No coincide el mensaje");
            }
        }else {
            throw new IllegalArgumentException("Mensaje no encontrado");
        }
    }

    @Override
    public void isDeleteMessage(Long id, String context) {
        Message message = getMessageById(id);
        if (message != null){
            if (message.getContext().equals(context)){
                message.setDelete(true);
                messageRepo.save(message);
            }else{
                throw new IllegalArgumentException("No coincide el mensaje");
            }
        }else {
            throw new IllegalArgumentException("Mensaje no encontrado");
        }
    }

    @Override
    public void sendMessage(Message message) {
        message.setId(null);
        message.setContext(message.getContext());
        message.setCreated(LocalDate.now());
        message.setRead(false);
        message.setDelete(false);
        message.setReceiver_User(message.getReceiver_User());
        message.setSender_User(message.getSender_User());
        messageRepo.save(message);
    }

    private Message getMessageById(Long id){
        return messageRepo.findById(id).orElse(null);
    }
}
