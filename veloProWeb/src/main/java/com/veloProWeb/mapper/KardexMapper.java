package com.veloProWeb.mapper;

import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.model.dto.inventory.KardexResponseDTO;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.model.entity.inventory.Kardex;
import com.veloProWeb.model.entity.product.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class KardexMapper {

    public Kardex toEntity(Product product, int quantity, String comment, MovementsType moves, User user){
        return Kardex.builder()
                .date(LocalDate.now())
                .quantity(quantity)
                .movementsType(moves)
                .comment(comment)
                .product(product)
                .price(product.getBuyPrice())
                .stock(product.getStock())
                .user(user)
                .build();
    }

    public KardexResponseDTO toResponseDTO(Kardex kardex){
        return KardexResponseDTO.builder()
                .date(kardex.getDate())
                .product(kardex.getProduct().getDescription())
                .stock(kardex.getStock())
                .price(kardex.getPrice())
                .movementsType(kardex.getMovementsType())
                .quantity(kardex.getQuantity())
                .user(String.format("%s %s", kardex.getUser().getName(), kardex.getUser().getSurname()))
                .comment(kardex.getComment())
                .build();
    }
}
