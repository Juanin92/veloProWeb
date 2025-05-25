package com.veloProWeb.model.dto.General;

import com.veloProWeb.model.dto.user.UserRequestDTO;
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
    private UserRequestDTO senderUser;
    private UserRequestDTO receiverUser;
    private String senderName;
}
