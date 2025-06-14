package com.veloproweb.model.dto.Report;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailySaleEarningDTO {
    private LocalDate date;
    private int profit;

    public DailySaleEarningDTO(LocalDate localDate, BigDecimal profit) {
        this.date = localDate;
        this.profit = profit != null ? profit.intValue() : 0;
    }
}
