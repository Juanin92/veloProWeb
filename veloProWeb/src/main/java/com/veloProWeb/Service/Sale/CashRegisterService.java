package com.veloProWeb.Service.Sale;

import com.veloProWeb.Model.Entity.Sale.CashRegister;
import com.veloProWeb.Repository.Sale.CashRegisterRepo;
import com.veloProWeb.Service.Sale.Interface.ICashRegisterService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CashRegisterService implements ICashRegisterService {

    @Autowired private CashRegisterRepo cashRegisterRepo;

    @Override
    public void addRegisterOpening(CashRegister cashRegister) {

    }

    @Override
    public void addRegisterClosing(CashRegister cashRegister) {

    }

    @Override
    public void addRegisterValidateComment(CashRegister cashRegister) {

    }

    @Override
    public CashRegister getRegisterByUser(Long id) {
        return null;
    }

    @Override
    public List<CashRegister> getAll() {
        return cashRegisterRepo.findAll();
    }

    @Override
    public void updateRegister(Long id, String status, Integer amountOpening, Integer amountClosing, Integer pos) {

    }
}
