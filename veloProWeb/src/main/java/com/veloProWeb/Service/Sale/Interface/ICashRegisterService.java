package com.veloProWeb.Service.Sale.Interface;

import com.veloProWeb.Model.DTO.CashRegisterDTO;

import java.util.List;

public interface ICashRegisterService {
    void addRegisterOpening(String username, int amount);
    void addRegisterClosing(CashRegisterDTO cashRegister);
    void addRegisterValidateComment(CashRegisterDTO cashRegister);
    List<CashRegisterDTO> getAll();
    void updateRegister(CashRegisterDTO cashRegister);
    boolean hasOpenRegisterOnDate(String username);
}
