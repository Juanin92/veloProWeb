package com.veloProWeb.Service.Sale.Interface;

import com.veloProWeb.Model.Entity.Sale.CashRegister;

import java.util.List;

public interface ICashRegisterService {
    void addRegisterOpening(CashRegister cashRegister);
    void addRegisterClosing(CashRegister cashRegister);
    void addRegisterValidateComment(CashRegister cashRegister);
    CashRegister getRegisterByUser(Long id);
    List<CashRegister> getAll();
    void updateRegister(Long id, String status, Integer amountOpening, Integer amountClosing, Integer pos);
}
