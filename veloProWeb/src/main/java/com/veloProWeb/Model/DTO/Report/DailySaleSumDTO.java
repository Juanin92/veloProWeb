package com.veloProWeb.Model.DTO.Report;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailySaleSumDTO {
    private LocalDate date;
    private int sum;

    public DailySaleSumDTO(LocalDate localDate, Integer sum) {
        this.date = localDate;
        this.sum = sum;
    }
}
