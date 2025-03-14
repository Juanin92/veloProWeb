package com.veloProWeb.Controller.User;

import com.veloProWeb.Model.DTO.General.MessageDTO;
import com.veloProWeb.Service.Record.IRecordService;
import com.veloProWeb.Service.User.Interface.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mensajes")
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

    @Autowired private IMessageService messageService;
    @Autowired private IRecordService recordService;

    @GetMapping()
    public ResponseEntity<List<MessageDTO>> getMessagesByUser(@AuthenticationPrincipal UserDetails userDetails){
        try{
            return ResponseEntity.ok(messageService.getMessageByUser(userDetails.getUsername()));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping()
    public ResponseEntity<Map<String, String>> isReadMessage(@RequestBody MessageDTO message,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            response.put("message","Mensaje leído");
            messageService.isReadMessage(message.getId(), message.getContext());
            recordService.registerAction(userDetails, "MESSAGE_SEEN",
                    "Mensaje visto ID: " + message.getId());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            recordService.registerAction(userDetails, "MESSAGE_SEEN_FAILURE",
                    "Error: Visto Mensaje ID: " + message.getId() + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("eliminar")
    public ResponseEntity<Map<String, String>> isDeleteMessage(@RequestBody MessageDTO message,
                                                               @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            response.put("message","Mensaje Borrado");
            messageService.isDeleteMessage(message.getId(), message.getContext());
            recordService.registerAction(userDetails, "MESSAGE_DELETE",
                    "Mensaje eliminado ID: " + message.getId());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            recordService.registerAction(userDetails, "MESSAGE_DELETE_FAILURE",
                    "Error: Eliminado Mensaje ID: " + message.getId() + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping()
    public ResponseEntity<Map<String,String>> sendMessage(@RequestBody MessageDTO message,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        Map<String, String> response = new HashMap<>();
        try{
            response.put("message","Mensaje Enviado");
            messageService.sendMessage(message, userDetails.getUsername());
            recordService.registerAction(userDetails, "MESSAGE",
                    "Envio Mensaje : " + message.getReceiverUser().getUsername() + " - '" + message.getContext() + "'");
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            recordService.registerAction(userDetails, "MESSAGE_FAILURE",
                    "Error: Envio Mensaje : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
