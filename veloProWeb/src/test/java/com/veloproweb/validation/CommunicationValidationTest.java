package com.veloproweb.validation;

import com.veloproweb.exceptions.communication.MessageAccessDeniedExcepcion;
import com.veloproweb.exceptions.communication.MessageAlreadyDeletedException;
import com.veloproweb.exceptions.communication.MessageAlreadyReadException;
import com.veloproweb.exceptions.communication.MessageReceiverUserException;
import com.veloproweb.model.entity.user.User;
import com.veloproweb.model.entity.communication.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommunicationValidationTest {

    @InjectMocks private CommunicationValidation validation;

    //Prueba que el usuario actual no sea el propietario del mensaje
    @Test
    void validateSenderAndReceiverAreDifferent() {
        Message message = Message.builder().receiverUser(User.builder().id(1L).username("johnny").build()).build();
        User currentUser = User.builder().id(1L).username("johnny").build();
        MessageReceiverUserException e = assertThrows(MessageReceiverUserException.class,
                () -> validation.validateSenderAndReceiverAreDifferent(message, currentUser));
        assertEquals("No puedes enviarte un mensaje a ti mismo", e.getMessage());
    }

    //Prueba para validar mensaje como leído
    @Test
    public void validateMessageNotRead(){
        Message message = Message.builder().receiverUser(User.builder().id(1L).username("johnny").build())
                .isRead(true).isDelete(false).build();
        MessageAlreadyReadException e = assertThrows(MessageAlreadyReadException.class,
                () -> validation.validateMessageAsRead(message, "johnny"));
        assertEquals("Este mensaje ya está marcado como leído", e.getMessage());
    }

    //Prueba para validar mensaje como leído
    @Test
    public void validateMessageNotDeleted(){
        Message message = Message.builder().receiverUser(User.builder().id(1L).username("johnny").build())
                .isDelete(true).build();
        MessageAlreadyDeletedException e = assertThrows(MessageAlreadyDeletedException.class,
                () -> validation.validateMessageAsDeleted(message, "johnny"));
        assertEquals("Este mensaje fue eliminado", e.getMessage());
    }

    //Prueba para verificar que el mensaje pertenece al usuario actual
    @Test
    public void validateMessageOwner(){
        Message message = Message.builder().receiverUser(User.builder().id(1L).username("johnny").build()).build();
        MessageAccessDeniedExcepcion e = assertThrows(MessageAccessDeniedExcepcion.class,
                () -> validation.validateMessageAsRead(message, "peter"));
        assertEquals("No tienes permiso para leer este mensaje", e.getMessage());
    }
}