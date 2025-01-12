package com.veloProWeb.Model.DTO.Report;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailySaleAvgDTO {
    private LocalDate date;
    private BigDecimal avg;

    public DailySaleAvgDTO(LocalDate localDate, BigDecimal avg) {
        this.date = localDate;
        this.avg = avg;
    }
}
