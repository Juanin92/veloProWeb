package com.veloProWeb.Model.DTO.General;

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
    private boolean isRead;
    private boolean isDelete;
    private Long senderUser;
    private Long receiverUser;
}
