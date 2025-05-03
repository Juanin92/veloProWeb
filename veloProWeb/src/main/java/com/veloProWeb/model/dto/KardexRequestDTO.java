package com.veloProWeb.model.dto;

import com.veloProWeb.model.Enum.MovementsType;
import lombok.Data;

@Data
public class KardexRequestDTO {
    private int quantity;
    private int stock;
    private String comment;
    private int price;
    private MovementsType movementsType;
    private Long idProduct;
//    private Long idUser;
}
