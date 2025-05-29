package com.veloProWeb.model.dto.communication;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequestDTO {

    @NotBlank(message = "Debe ingresar una descripción a la tarea")
    private String description;

    @NotBlank(message = "Debe seleccionar un usuario para asignar a la tarea")
    private String user;
}
