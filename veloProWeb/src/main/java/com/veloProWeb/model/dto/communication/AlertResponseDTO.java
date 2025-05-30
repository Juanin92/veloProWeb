package com.veloProWeb.model.dto.communication;

import com.veloProWeb.model.Enum.AlertStatus;
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
