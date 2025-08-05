package com.veloproweb.model.dto.inventory;

import com.veloproweb.model.enums.MovementsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KardexResponseDTO {

    private LocalDate date;
    private String product;
    private int stock;
    private int price;
    private MovementsType movementsType;
    private int quantity;
    private String user;
    private String comment;
}
