package com.veloProWeb.Model.DTO.General;

import com.veloProWeb.Model.DTO.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private Long id;
    private String context;
    private LocalDate created;
    private boolean read;
    private boolean delete;
    private UserDTO senderUser;
    private UserDTO receiverUser;
    private String senderName;
}
