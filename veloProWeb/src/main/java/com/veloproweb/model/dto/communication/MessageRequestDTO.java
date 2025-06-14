package com.veloproweb.model.dto.communication;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequestDTO {

    @NotBlank(message = "El contenido del mensaje es obligatorio")
    private String context;

    @NotBlank(message = "El usuario receptor es obligatorio")
    private String receiverUser;
}
