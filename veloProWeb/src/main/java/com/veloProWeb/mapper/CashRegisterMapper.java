package com.veloProWeb.mapper;

import com.veloProWeb.model.dto.sale.CashRegisterRequestDTO;
import com.veloProWeb.model.dto.sale.CashRegisterResponseDTO;
import com.veloProWeb.model.entity.Sale.CashRegister;
import com.veloProWeb.model.entity.User.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CashRegisterMapper {

    public CashRegister toOpeningRegisterEntity(User user, int amount){
        return CashRegister.builder()
                .dateOpening(LocalDateTime.now())
                .dateClosing(null)
                .amountOpening(amount)
                .amountClosingCash(0)
                .amountClosingPos(0)
                .status("OPEN")
                .comment(null)
                .alert(false)
                .user(user)
                .build();
    }

    public CashRegisterResponseDTO toResponseDTO(CashRegister cashRegister){
        return CashRegisterResponseDTO.builder()
                .id(cashRegister.getId())
                .dateOpening(cashRegister.getDateOpening())
                .dateClosing(cashRegister.getDateClosing())
                .amountOpening(cashRegister.getAmountOpening())
                .amountClosingCash(cashRegister.getAmountClosingCash())
                .amountClosingPos(cashRegister.getAmountClosingPos())
                .status(cashRegister.getStatus())
                .comment(cashRegister.getComment())
                .alert(cashRegister.isAlert())
                .user(String.format("%s %s", cashRegister.getUser().getName(), cashRegister.getUser().getSurname()))
                .build();
    }
}
