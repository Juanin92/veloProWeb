package com.veloProWeb.Model.DTO.Report;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailySaleEarningDTO {
    private LocalDate saleDTO;
    private int profit;

    public DailySaleEarningDTO(LocalDate localDate, BigDecimal profit) {
        this.saleDTO = localDate;
        this.profit = profit != null ? profit.intValue() : 0;
    }
}
