package com.veloProWeb.Service.Sale.Interface;

import com.veloProWeb.Model.DTO.CashRegisterDTO;
import com.veloProWeb.Model.Entity.Sale.CashRegister;

import java.util.List;

public interface ICashRegisterService {
    void addRegisterOpening(String username, int amount);
    void addRegisterClosing(CashRegisterDTO cashRegister);
    List<CashRegisterDTO> getAll();
    void updateRegister(CashRegisterDTO cashRegister);
    boolean hasOpenRegisterOnDate(String username);
}
