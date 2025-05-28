package com.veloProWeb.service.communication;

import com.veloProWeb.exceptions.communication.MessageNotFoundException;
import com.veloProWeb.mapper.MessageMapper;
import com.veloProWeb.model.dto.communication.MessageRequestDTO;
import com.veloProWeb.model.dto.communication.MessageResponseDTO;
import com.veloProWeb.model.entity.communication.Message;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.repository.communication.MessageRepo;
import com.veloProWeb.service.communication.interfaces.IMessageService;
import com.veloProWeb.service.user.Interface.IUserService;
import com.veloProWeb.validation.CommunicationValidation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageService implements IMessageService {

    private final MessageRepo messageRepo;
    private final IUserService userService;
    private final MessageMapper mapper;
    private final CommunicationValidation validation;

    /**
     * Obtener mensajes de un usuario específico.
     * Filtra la lista de mensajes para que no contenga mensajes borrados
     *
     * @param username - nombre de usuario del usuario
     * @return - Lista de mensajes del usuario en objeto DTO
     */
    @Override
    public List<MessageResponseDTO> getMessageByUser(String username) {
        User user = userService.getUserByUsername(username);
        return messageRepo.findByReceiverUserAndIsDeleteFalse(user).stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * Dejar el mensaje como visto en el registro.
     * @param id - Identificador del mensaje
     * @param username - nombre de usuario dueño del mensaje
     */
    @Transactional
    @Override
    public void markMessageAsRead(Long id, String username) {
        Message message = getMessageById(id);
        validation.validateMessageAsRead(message, username);
        message.setRead(true);
        messageRepo.save(message);
    }

    /**
     * Eliminar el mensaje en el registro.
     *
     * @param id - Identificador del mensaje
     * @param username - nombre de usuario dueño del mensaje
     */
    @Transactional
    @Override
    public void markMessageAsDeleted(Long id, String username) {
        Message message = getMessageById(id);
        validation.validateMessageAsDeleted(message, username);
        message.setDelete(true);
        messageRepo.save(message);
    }

    /**
     * Crea y Envia mensaje entre usuarios.
     * Verifica que los usuarios receptor y destinatario existan y no sean el mismo usuario.
     * @param dto - Objeto DTO con los datos necesarios
     * @param senderUsername - nombre de usuario del usuario remitente
     */
    @Transactional
    @Override
    public void sendMessage(MessageRequestDTO dto, String senderUsername) {
        User receiverUser = userService.getUserByUsername(dto.getReceiverUser());
        User senderUser = userService.getUserByUsername(senderUsername);

        Message message = mapper.toEntity(dto.getContext(), senderUser, receiverUser);
        validation.validateSenderAndReceiverAreDifferent(message, receiverUser);
        messageRepo.save(message);
    }

    /**
     * Buscar un mensaje por su identificador
     * @param id - Identificador del mensaje
     * @return - Si encuentra el objeto un Mensaje o lanza una excepción
     */
    private Message getMessageById(Long id){
        return messageRepo.findById(id).orElseThrow(() -> new MessageNotFoundException("Mensaje no encontrado"));
    }
}
