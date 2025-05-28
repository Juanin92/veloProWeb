package com.veloProWeb.controller.communication;

import com.veloProWeb.model.dto.communication.MessageRequestDTO;
import com.veloProWeb.model.dto.communication.MessageResponseDTO;
import com.veloProWeb.service.reporting.interfaces.IRecordService;
import com.veloProWeb.service.communication.interfaces.IMessageService;
import com.veloProWeb.util.ResponseMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mensajes")
@AllArgsConstructor
@Validated
public class MessageController {

    private final IMessageService messageService;
    private final IRecordService recordService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE', 'GUEST', 'SELLER')")
    public ResponseEntity<List<MessageResponseDTO>> getMessagesByUser(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(messageService.getMessageByUser(userDetails.getUsername()));
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE', 'GUEST', 'SELLER')")
    public ResponseEntity<Map<String, String>> markMessageAsRead(@RequestParam
                                                                     @NotNull(message = "El ID es obligatorio") Long id,
                                                                 @AuthenticationPrincipal UserDetails userDetails){
        messageService.markMessageAsRead(id, userDetails.getUsername());
        recordService.registerAction(userDetails, "MESSAGE_SEEN","Mensaje visto ID: " + id);
        return new ResponseEntity<>(ResponseMessage.message("Mensaje leído"), HttpStatus.OK);
    }

    @PutMapping("eliminar")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE', 'GUEST', 'SELLER')")
    public ResponseEntity<Map<String, String>> markMessageAsDeleted(@RequestParam
                                                                        @NotNull(message = "El ID es obligatorio") Long id,
                                                                    @AuthenticationPrincipal UserDetails userDetails){
        messageService.markMessageAsDeleted(id, userDetails.getUsername());
        recordService.registerAction(userDetails, "MESSAGE_DELETE", "Mensaje eliminado ID: " + id);
        return new ResponseEntity<>(ResponseMessage.message("Mensaje Borrado"), HttpStatus.OK);
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE', 'GUEST', 'SELLER')")
    public ResponseEntity<Map<String,String>> sendMessage(@RequestBody @Valid MessageRequestDTO message,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        messageService.sendMessage(message, userDetails.getUsername());
        recordService.registerAction(userDetails, "MESSAGE",
                String.format("Envio Mensaje (%S): '%s'", message.getReceiverUser(), message.getContext()));
        return new ResponseEntity<>(ResponseMessage.message("Mensaje Enviado"), HttpStatus.CREATED);
    }
}
