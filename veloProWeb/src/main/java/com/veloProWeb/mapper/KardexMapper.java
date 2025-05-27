package com.veloProWeb.mapper;

import com.veloProWeb.model.dto.KardexResponseDTO;
import com.veloProWeb.model.entity.inventory.Kardex;
import org.springframework.stereotype.Component;

@Component
public class KardexMapper {

    public KardexResponseDTO toResponseDTO(Kardex kardex){
        return KardexResponseDTO.builder()
                .date(kardex.getDate())
                .product(kardex.getProduct().getDescription())
                .stock(kardex.getStock())
                .price(kardex.getPrice())
                .movementsType(kardex.getMovementsType())
                .quantity(kardex.getQuantity())
                .user(String.format("%s %S", kardex.getUser().getName(), kardex.getUser().getSurname()))
                .comment(kardex.getComment())
                .build();
    }
}
