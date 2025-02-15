package com.veloProWeb.Service;

import com.veloProWeb.Model.DTO.General.MessageDTO;
import com.veloProWeb.Model.Entity.User.Message;
import com.veloProWeb.Model.Entity.User.User;
import com.veloProWeb.Repository.MessageRepo;
import com.veloProWeb.Repository.UserRepo;
import com.veloProWeb.Service.User.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @InjectMocks private MessageService messageService;
    @Mock private MessageRepo messageRepo;
    @Mock private UserRepo userRepo;
    private Message message;
    private Message message2;
    private User userSender;
    private User userReceiver;

    @BeforeEach
    void setUp(){
        userSender = new User();
        userSender.setId(1L);
        userSender.setName("Juan");
        userReceiver = new User();
        userReceiver.setId(2L);
        userReceiver.setName("Pedro");

        message = new Message();
        message.setId(1L);
        message.setRead(false);
        message.setDelete(false);
        message.setCreated(LocalDate.now());
        message.setContext("Prueba 1");
        message.setSenderUser(userSender);
        message.setReceiverUser(userReceiver);

        message2 = new Message();
        message2.setId(1L);
        message2.setRead(false);
        message2.setDelete(false);
        message2.setCreated(LocalDate.now());
        message2.setContext("Prueba 2");
        message2.setSenderUser(userReceiver);
        message2.setReceiverUser(userSender);
    }

    //Prueba para obtener todos los mensajes en el sistema
    @Test
    public void getAllMessages_valid(){
        List<Message> messageList = Arrays.asList(message,message2);
        when(messageService.getAllMessages()).thenReturn(messageList);
        List<Message> result = messageService.getAllMessages();
        verify(messageRepo).findAll();
        assertEquals(messageList, result);
    }

    //Prueba para obtener los mensajes de un usuario
    @Test
    public void getMessageByUser_valid(){
        List<Message> messageList = Collections.singletonList(message);
        when(userRepo.findById(1L)).thenReturn(Optional.of(userSender));
        when(messageRepo.findByReceiverUser(userSender)).thenReturn(messageList);

        List<MessageDTO> dtoList = messageService.getMessageByUser(userSender.getId());
        verify(messageRepo).findByReceiverUser(userSender);
        assertEquals(messageList.size(), dtoList.size());
    }
    @Test
    public void getMessageByUser_invalid(){
        when(userRepo.findById(3L)).thenReturn(Optional.empty());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,() -> messageService.getMessageByUser(3L));

        verify(messageRepo, never()).findByReceiverUser(userSender);
        assertEquals("Usuario no encontrado", e.getMessage());
    }

    //Prueba para dejar como visto el mensaje
    @Test
    public void isReadMessage_valid(){
        when(messageRepo.findById(1L)).thenReturn(Optional.of(message));
        messageService.isReadMessage(1L, "Prueba 1");

        verify(messageRepo, times(1)).findById(1L);
        verify(messageRepo, times(1)).save(message);
        assertEquals("Prueba 1", message.getContext());
        assertTrue(message.isRead());
    }
    @Test
    public void isReadMessage_validNullMessage(){
        when(messageRepo.findById(5L)).thenReturn(Optional.empty());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> messageService.isReadMessage(5L, "Prueba"));

        verify(messageRepo, times(1)).findById(5L);
        verify(messageRepo, never()).save(message);
        assertEquals("Mensaje no encontrado", e.getMessage());
    }
    @Test
    public void isReadMessage_validNoMatchContext(){
        when(messageRepo.findById(2L)).thenReturn(Optional.of(message2));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> messageService.isReadMessage(2L, "Prueba Extra"));

        verify(messageRepo, times(1)).findById(2L);
        verify(messageRepo, never()).save(message);
        assertEquals("No coincide el mensaje", e.getMessage());
    }

    //Prueba para eliminar el mensaje
    @Test
    public void isDeleteMessage_valid(){
        when(messageRepo.findById(1L)).thenReturn(Optional.of(message));
        messageService.isDeleteMessage(1L, "Prueba 1");

        verify(messageRepo, times(1)).findById(1L);
        verify(messageRepo, times(1)).save(message);
        assertEquals("Prueba 1", message.getContext());
        assertTrue(message.isDelete());
    }
    @Test
    public void isDeleteMessage_validNullMessage(){
        when(messageRepo.findById(5L)).thenReturn(Optional.empty());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> messageService.isDeleteMessage(5L, "Prueba"));

        verify(messageRepo, times(1)).findById(5L);
        verify(messageRepo, never()).save(message);
        assertEquals("Mensaje no encontrado", e.getMessage());
    }
    @Test
    public void isDeleteMessage_validNoMatchContext(){
        when(messageRepo.findById(2L)).thenReturn(Optional.of(message2));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> messageService.isDeleteMessage(2L, "Prueba Extra"));

        verify(messageRepo, times(1)).findById(2L);
        verify(messageRepo, never()).save(message);
        assertEquals("No coincide el mensaje", e.getMessage());
    }

    //Prueba para enviar mensaje
    @Test
    public void sendMessage_valid(){
        MessageDTO dto = new MessageDTO(null, "Prueba 1", null,false, false, userSender.getId(), userReceiver.getId(), "senderName");
        when(userRepo.findById(dto.getSenderUser())).thenReturn(Optional.of(userSender));
        when(userRepo.findById(dto.getReceiverUser())).thenReturn(Optional.of(userReceiver));
        messageService.sendMessage(dto);

        verify(messageRepo, times(1)).save(any(Message.class));
        assertEquals("Prueba 1", message.getContext());
        assertEquals(1L, message.getSenderUser().getId());
        assertEquals(2L, message.getReceiverUser().getId());
    }
}
