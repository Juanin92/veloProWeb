package com.veloProWeb.validation;

import com.veloProWeb.exceptions.sale.CashRegisterDateNotMatchException;
import com.veloProWeb.exceptions.sale.CashRegisterNotFoundException;
import com.veloProWeb.exceptions.sale.InvalidAmountCashRegisterException;
import com.veloProWeb.exceptions.sale.UnauthorizedCashRegisterAccessException;
import com.veloProWeb.model.dto.sale.CashRegisterRequestDTO;
import com.veloProWeb.model.entity.Sale.CashRegister;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CashRegisterValidator {

    public void validateCloseRegister(CashRegisterRequestDTO dto, CashRegister cashRegister){
        validateAmount(dto.getAmountClosingCash());
        validateAmount(dto.getAmountClosingPos());
        validateCashRegisterExists(cashRegister);
        validateCashRegisterDate(cashRegister);
    }

    /**
     * Validar los montos ingresados
     * @param amount - Monto a validar
     */
    public void validateAmount(int amount){
        if (amount <= 0) {
            throw new InvalidAmountCashRegisterException("El monto de la caja debe ser mayor a 0");
        }
    }

    public void validateCashRegisterExists(CashRegister cashRegister){
        if (cashRegister == null) {
            throw new CashRegisterNotFoundException("No hay registro de apertura válido.");
        }
    }

    public void validateCashRegisterDate(CashRegister cashRegister){
        if (cashRegister.getDateOpening().isAfter(LocalDateTime.now())) {
            throw new CashRegisterDateNotMatchException("La fecha no coincide con la apertura.");
        }
    }

    public void validateRoleCanRegister(Boolean canNotRegister){
        if (canNotRegister) {
            throw new UnauthorizedCashRegisterAccessException("Este rol no puede operar con registros de caja");
        }
    }
}
