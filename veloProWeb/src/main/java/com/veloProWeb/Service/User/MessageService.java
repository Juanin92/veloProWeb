package com.veloProWeb.Service.User;

import com.veloProWeb.Model.DTO.General.MessageDTO;
import com.veloProWeb.Model.Entity.User.Message;
import com.veloProWeb.Model.Entity.User.User;
import com.veloProWeb.Repository.MessageRepo;
import com.veloProWeb.Repository.UserRepo;
import com.veloProWeb.Service.User.Interface.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MessageService implements IMessageService {

    @Autowired private MessageRepo messageRepo;
    @Autowired private UserRepo userRepo;

    /**
     * Obtiene todos los mensajes del sistema
     * @return - Lista con todos los mensajes
     */
    @Override
    public List<Message> getAllMessages() {
        return messageRepo.findAll();
    }

    /**
     * Obtener mensajes de un usuario específico
     * Verifica que el usuario exista
     * @param userID - Identificador del usuario
     * @return - Lista de mensajes del usuario
     */
    @Override
    public List<Message> getMessageByUser(Long userID) {
        User user = userRepo.findById(userID).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return messageRepo.findByReceiverUser(user);
    }

    /**
     * Dejar el mensaje como visto en el registro.
     * Verifica que el mensaje exista y que si existe sus mensajes coincidan
     * @param id - Identificador del mensaje
     * @param context - Cadena contiene el mensaje
     */
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

    /**
     * Eliminar el mensaje en el registro.
     * Verifica que el mensaje exista y que si existe sus mensajes coincidan
     * @param id - Identificador del mensaje
     * @param context - Cadena contiene el mensaje
     */
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

    /**
     * Crea y Envia mensaje entre usuarios
     * Verifica que los usuarios receptor y destinatario existan
     * @param dto - Objeto DTO con los datos necesarios
     */
    @Override
    public void sendMessage(MessageDTO dto) {
        User receiverUser = userRepo.findById(dto.getReceiverUser()).orElseThrow(() -> new IllegalArgumentException("Destinatario no encontrado"));
        User senderUser = userRepo.findById(dto.getSenderUser()).orElseThrow(() -> new IllegalArgumentException("Receptor no encontrado"));

        Message message =  new Message();
        message.setId(null);
        message.setContext(dto.getContext());
        message.setCreated(LocalDate.now());
        message.setRead(false);
        message.setDelete(false);
        message.setReceiverUser(receiverUser);
        message.setSenderUser(senderUser);
        messageRepo.save(message);
    }

    /**
     * Buscar un mensaje por su identificador
     * @param id - Identificador del mensaje
     * @return - Si encuentra el objeto un Mensaje o valor nulo en el caso contrario
     */
    private Message getMessageById(Long id){
        return messageRepo.findById(id).orElse(null);
    }
}
