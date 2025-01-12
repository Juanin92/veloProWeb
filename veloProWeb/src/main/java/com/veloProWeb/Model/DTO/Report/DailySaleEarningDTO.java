package com.veloProWeb.Model.DTO.Report;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailySaleEarningDTO {
    private LocalDate saleDTO;
    private int profit;
}
