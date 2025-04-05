package com.veloProWeb.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashRegisterDTO {

    private Long id;
    private LocalDateTime dateOpening;
    private LocalDateTime dateClosing;
    private int amountOpening;
    private int amountClosingCash;
    private int amountClosingPos;
    private String status;
    private String comment;
    private boolean alert;
    private String user;
}
