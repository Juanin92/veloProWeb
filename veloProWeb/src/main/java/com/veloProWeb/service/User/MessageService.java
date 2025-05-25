package com.veloProWeb.service.User;

import com.veloProWeb.model.dto.General.MessageDTO;
import com.veloProWeb.model.dto.user.UserRequestDTO;
import com.veloProWeb.model.entity.User.Message;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.repository.MessageRepo;
import com.veloProWeb.service.User.Interface.IMessageService;
import com.veloProWeb.service.User.Interface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService implements IMessageService {

    @Autowired private MessageRepo messageRepo;
    @Autowired private IUserService userService;

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
     * Filtra la lista de mensajes para que no contenga mensajes borrados
     * @param username - nombre de usuario del usuario
     * @return - Lista de mensajes del usuario
     */
    @Override
    public List<MessageDTO> getMessageByUser(String username) {
        User user = userService.getUserWithUsername(username);
        List<MessageDTO> messageDTOList = new ArrayList<>();
        for (Message message: messageRepo.findByReceiverUser(user)){
            MessageDTO dto = new MessageDTO();
            dto.setId(message.getId());
            dto.setContext(message.getContext());
            dto.setCreated(message.getCreated());
            dto.setRead(message.isRead());
            dto.setDelete(message.isDelete());
            dto.setReceiverUser(createUserDTO(user));
            dto.setSenderUser(createUserDTO(message.getSenderUser()));
            dto.setSenderName(message.getSenderUser().getName() + " " + message.getSenderUser().getSurname());
            messageDTOList.add(dto);
        }
        return messageDTOList.stream()
                .filter(message -> !message.isDelete())
                .toList();
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
     * @param dto - Objeto dto con los datos necesarios
     * @param senderUsername - nombre de usuario del usuario remitente
     */
    @Override
    public void sendMessage(MessageDTO dto, String senderUsername) {
        User receiverUser = userService.getUserWithUsername(dto.getReceiverUser().getUsername());
        User senderUser = userService.getUserWithUsername(senderUsername);

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

    /**
     * Crear un objeto dto de usuario
     * @param user - Usuario a convertir en dto
     * @return - Objeto dto con los datos del usuario
     */
    private UserRequestDTO createUserDTO(User user){
        return new UserRequestDTO(
                user.getName(), user.getSurname(), user.getUsername(),
                user.getRut(), user.getEmail(), user.getRole(), user.isStatus(), user.getDate());
    }
}
