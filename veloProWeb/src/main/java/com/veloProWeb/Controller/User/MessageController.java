package com.veloProWeb.Controller.User;

import com.veloProWeb.Model.DTO.General.MessageDTO;
import com.veloProWeb.Model.Entity.User.Message;
import com.veloProWeb.Service.User.Interface.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mensajes")
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

    @Autowired private IMessageService messageService;

    @GetMapping()
    public ResponseEntity<List<MessageDTO>> getMessagesByUser(@RequestParam Long userId){
        try{
            return ResponseEntity.ok(messageService.getMessageByUser(userId));
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping()
    public ResponseEntity<Map<String, String>> isReadMessage(@RequestBody MessageDTO message){
        Map<String, String> response = new HashMap<>();
        try{
            response.put("message","Mensaje leído");
            messageService.isReadMessage(message.getId(), message.getContext());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("eliminar")
    public ResponseEntity<Map<String, String>> isDeleteMessage(@RequestBody MessageDTO message){
        Map<String, String> response = new HashMap<>();
        try{
            response.put("message","Mensaje Borrado");
            messageService.isDeleteMessage(message.getId(), message.getContext());
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping()
    public ResponseEntity<Map<String,String>> sendMessage(@RequestBody MessageDTO message){
        Map<String, String> response = new HashMap<>();
        try{
            response.put("message","Mensaje Enviado");
            messageService.sendMessage(message);
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
