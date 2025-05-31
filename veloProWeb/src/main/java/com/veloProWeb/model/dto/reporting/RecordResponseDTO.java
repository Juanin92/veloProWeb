package com.veloProWeb.model.dto.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordResponseDTO {

    private LocalDateTime entryDate;
    private LocalDateTime endaDate;
    private LocalDateTime actionDate;
    private String action;
    private String comment;
    private String user;
}
