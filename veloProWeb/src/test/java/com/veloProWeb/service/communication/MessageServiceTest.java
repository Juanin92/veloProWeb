package com.veloProWeb.service.communication;

import com.veloProWeb.exceptions.communication.MessageNotFoundException;
import com.veloProWeb.exceptions.communication.MessageReceiverUserException;
import com.veloProWeb.mapper.MessageMapper;
import com.veloProWeb.model.dto.communication.MessageRequestDTO;
import com.veloProWeb.model.dto.communication.MessageResponseDTO;
import com.veloProWeb.model.entity.communication.Message;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.repository.communication.MessageRepo;
import com.veloProWeb.service.user.UserService;
import com.veloProWeb.validation.CommunicationValidation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @InjectMocks private MessageService messageService;
    @Mock private MessageRepo messageRepo;
    @Mock private UserService userService;
    @Spy private MessageMapper mapper;
    @Mock private CommunicationValidation validation;

    //Prueba para obtener los mensajes de un usuario
    @Test
    void getMessageByUser() {
        User user = User.builder().id(1L).name("John").surname("Doe").username("Johnny").build();
        when(userService.getUserByUsername("johnny")).thenReturn(user);
        Message message = Message.builder().id(1L).context("test").isRead(false).isDelete(false).receiverUser(user)
                .senderUser(User.builder().name("Peter").surname("Jackson").build()).build();
        Message message2 = Message.builder().id(3L).context("test 1").isRead(false).isDelete(false).receiverUser(user)
                .senderUser(User.builder().name("Bill").surname("Gates").build()).build();
        when(messageRepo.findByReceiverUserAndIsDeleteFalse(user)).thenReturn(List.of(message, message2));

        MessageResponseDTO mappedMessage = MessageResponseDTO.builder().id(1L).context("test").read(false).delete(false)
                .senderName("Peter Jackson").build();
        MessageResponseDTO mappedMessage2 = MessageResponseDTO.builder().id(3L).context("test 1").read(false)
                .delete(false).senderName("Bill Gates").build();
        when(mapper.toResponse(message)).thenReturn(mappedMessage);
        when(mapper.toResponse(message2)).thenReturn(mappedMessage2);
        List<MessageResponseDTO> messageResponseDTOS = List.of(mappedMessage, mappedMessage2);

        List<MessageResponseDTO> result = messageService.getMessageByUser("johnny");

        verify(userService, times(1)).getUserByUsername("johnny");
        verify(messageRepo, times(1)).findByReceiverUserAndIsDeleteFalse(user);
        verify(mapper, times(2)).toResponse(any(Message.class));

        assertEquals(messageResponseDTOS.size(), result.size());
        assertEquals("Peter Jackson", result.getFirst().getSenderName());
    }

    //Prueba para dejar como leído el mensaje
    @Test
    void markMessageAsRead() {
        Message message = Message.builder().id(1L).context("Test").isRead(false).isDelete(false).build();
        when(messageRepo.findById(1L)).thenReturn(Optional.of(message));
        doNothing().when(validation).validateMessageAsRead(message, "johnny");

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        messageService.markMessageAsRead(1L, "johnny");

        verify(messageRepo, times(1)).findById(1L);
        verify(validation, times(1)).validateMessageAsRead(message, "johnny");
        verify(messageRepo, times(1)).save(messageCaptor.capture());

        Message resultMessage = messageCaptor.getValue();
        assertTrue(resultMessage.isRead());
    }
    @Test
    void markMessageAsRead_messageNotFound() {
        when(messageRepo.findById(2L)).thenReturn(Optional.empty());

        MessageNotFoundException e = assertThrows(MessageNotFoundException.class,
                () -> messageService.markMessageAsRead(2L, "johnny"));

        verify(messageRepo, times(1)).findById(2L);
        verify(validation, never()).validateMessageAsRead(any(Message.class), eq("johnny"));
        verify(messageRepo, never()).save(any(Message.class));

        assertEquals("Mensaje no encontrado", e.getMessage());
    }

    //Prueba para eliminar el mensaje
    @Test
    void markMessageAsDeleted() {
        Message message = Message.builder().id(1L).context("Test").isRead(true).isDelete(false).build();
        when(messageRepo.findById(1L)).thenReturn(Optional.of(message));
        doNothing().when(validation).validateMessageAsDeleted(message, "johnny");

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        messageService.markMessageAsDeleted(1L, "johnny");

        verify(messageRepo, times(1)).findById(1L);
        verify(validation, times(1)).validateMessageAsDeleted(message, "johnny");
        verify(messageRepo, times(1)).save(messageCaptor.capture());

        Message resultMessage = messageCaptor.getValue();
        assertTrue(resultMessage.isDelete());
    }

    //Prueba para enviar mensaje
    @Test
    void sendMessage() {
        MessageRequestDTO dto = MessageRequestDTO.builder().context("test").receiverUser("johnny").build();
        User receiverUser = User.builder().id(1L).name("John").surname("Doe").username("johnny").build();
        User senderUser = User.builder().id(5L).name("Peter").surname("Gates").username("peter").build();
        when(userService.getUserByUsername(dto.getReceiverUser())).thenReturn(receiverUser);
        when(userService.getUserByUsername("peter")).thenReturn(senderUser);

        Message message = Message.builder().id(1L).context("test").isDelete(false).isRead(false).senderUser(senderUser)
                .receiverUser(receiverUser).created(LocalDate.now()).build();
        when(mapper.toEntity(dto.getContext(), senderUser, receiverUser)).thenReturn(message);
        doNothing().when(validation).validateSenderAndReceiverAreDifferent(message, receiverUser);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        messageService.sendMessage(dto, "peter");

        verify(userService, times(2)).getUserByUsername(anyString());
        verify(mapper, times(1)).toEntity(dto.getContext(), senderUser, receiverUser);
        verify(validation, times(1)).validateSenderAndReceiverAreDifferent(message, receiverUser);
        verify(messageRepo, times(1)).save(messageCaptor.capture());

        Message resultMessage = messageCaptor.getValue();
        assertEquals("test", resultMessage.getContext());
        assertEquals(receiverUser, resultMessage.getReceiverUser());
        assertEquals(senderUser, resultMessage.getSenderUser());
    }
    @Test
    void sendMessage_receiverUserEqualSenderUserException() {
        MessageRequestDTO dto = MessageRequestDTO.builder().context("test").receiverUser("johnny").build();
        User receiverUser = User.builder().id(1L).name("John").surname("Doe").username("johnny").build();
        User senderUser = User.builder().id(1L).name("John").surname("Doe").username("johnny").build();
        when(userService.getUserByUsername(dto.getReceiverUser())).thenReturn(receiverUser);
        when(userService.getUserByUsername("johnny")).thenReturn(senderUser);

        Message message = Message.builder().id(1L).context("test").isDelete(false).isRead(false).senderUser(senderUser)
                .receiverUser(receiverUser).created(LocalDate.now()).build();
        when(mapper.toEntity(dto.getContext(), senderUser, receiverUser)).thenReturn(message);
        doThrow(new MessageReceiverUserException("No puedes enviarte un mensaje a ti mismo")).when(validation)
                .validateSenderAndReceiverAreDifferent(message, receiverUser);

        MessageReceiverUserException e = assertThrows(MessageReceiverUserException.class,
                () -> messageService.sendMessage(dto, "johnny"));

        verify(userService, times(2)).getUserByUsername(anyString());
        verify(mapper, times(1)).toEntity(dto.getContext(), senderUser, receiverUser);
        verify(validation, times(1)).validateSenderAndReceiverAreDifferent(message, receiverUser);
        verify(messageRepo, never()).save(any(Message.class));

        assertEquals("No puedes enviarte un mensaje a ti mismo", e.getMessage());
    }
}
