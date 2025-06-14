package com.veloproweb.model.dto.report;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailySaleCountDTO {
    private LocalDate date;
    private int sale;

    public DailySaleCountDTO(LocalDate localDate, Long count) {
        this.date = localDate;
        this.sale = count.intValue();
    }
}
