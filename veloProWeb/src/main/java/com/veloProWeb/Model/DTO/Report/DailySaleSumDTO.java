package com.veloProWeb.Model.DTO.Report;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailySaleSumDTO {
    private LocalDate date;
    private BigDecimal sum;

    public DailySaleSumDTO(LocalDate localDate, BigDecimal sum) {
        this.date = localDate;
        this.sum = sum;
    }
}
