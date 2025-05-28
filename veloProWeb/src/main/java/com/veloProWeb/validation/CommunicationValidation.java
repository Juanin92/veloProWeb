package com.veloProWeb.validation;

import com.veloProWeb.exceptions.communication.MessageAccessDeniedExcepcion;
import com.veloProWeb.exceptions.communication.MessageAlreadyDeletedException;
import com.veloProWeb.exceptions.communication.MessageAlreadyReadException;
import com.veloProWeb.exceptions.communication.MessageReceiverUserException;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.model.entity.communication.Message;
import org.springframework.stereotype.Component;

@Component
public class CommunicationValidation {

    // Validación de mensajes como leídos
    public void validateMessageAsRead(Message message, String currentUser) {
        validateMessageOwner(message, currentUser);
        validateMessageNotDeleted(message);
        validateMessageNotRead(message);
    }

    // Validación de mensajes como eliminados
    public void validateMessageAsDeleted(Message message, String currentUser) {
        validateMessageOwner(message, currentUser);
        validateMessageNotDeleted(message);
    }

    //¨Validación de mensajes enviados por el mismo usuario
    public void validateSenderAndReceiverAreDifferent(Message message, User currentUser){
        if (message.getReceiverUser().equals(currentUser)) {
            throw new MessageReceiverUserException("No puedes enviarte un mensaje a ti mismo");
        }
    }

    // Validación de mensajes ya leídos
    private void validateMessageNotRead(Message message){
        if (message.isRead()) {
            throw new MessageAlreadyReadException("Este mensaje ya está marcado como leído");
        }
    }

    // Validación de mensajes ya eliminados
    private void validateMessageNotDeleted(Message message){
        if (message.isDelete()) {
            throw new MessageAlreadyDeletedException("Este mensaje fue eliminado");
        }
    }

    // Validación de que el usuario actual es el propietario del mensaje
    private void validateMessageOwner(Message message, String currentUser){
        if (!message.getReceiverUser().getUsername().equals(currentUser)) {
            throw new MessageAccessDeniedExcepcion("No tienes permiso para leer este mensaje");
        }
    }
}
