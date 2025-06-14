package com.veloproweb.model.dto.communication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDTO {

    private Long id;
    private String context;
    private LocalDate created;
    private boolean read;
    private boolean delete;
    private String senderName;
}
