package com.veloproweb.model.dto.communication;

import com.veloproweb.model.enums.AlertStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertResponseDTO {

    private Long id;
    private String description;
    private AlertStatus status;
    private LocalDate created;
}
