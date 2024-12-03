package com.veloProWeb.Service.Costumer;

import com.veloProWeb.Model.Entity.Costumer.Costumer;

import java.util.List;

public interface ICostumerService {
    void addNewCostumer(Costumer costumer);
    List<Costumer> getAll();
    void delete(Costumer costumer);
    void paymentDebt(Costumer costumer,String amount);
    void statusAssign(Costumer costumer);
    void addSaleToCostumer(Costumer costumer);
    void updateTotalDebt(Costumer costumer);
    void updateCostumer(Costumer costumer);
}
