package com.veloProWeb.service.Sale.Interface;

import com.veloProWeb.model.dto.CashRegisterDTO;

import java.util.List;

public interface ICashRegisterService {
    void addRegisterOpening(String username, int amount);
    void addRegisterClosing(String username, CashRegisterDTO cashRegister);
    List<CashRegisterDTO> getAll();
    void updateRegister(CashRegisterDTO cashRegister);
    boolean hasOpenRegisterOnDate(String username);
}
