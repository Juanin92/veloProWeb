package com.veloProWeb.Model.DTO.Report;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailySaleSumDTO {
    private LocalDate date;
    private int sum;
}
