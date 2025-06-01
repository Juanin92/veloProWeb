package com.veloProWeb.service.sale.Interface;

import com.veloProWeb.model.dto.sale.CashRegisterRequestDTO;
import com.veloProWeb.model.dto.sale.CashRegisterResponseDTO;

import java.util.List;

public interface ICashRegisterService {
    void openRegister(String username, int amount);
    void closeRegister(String username, CashRegisterRequestDTO cashRegister);
    List<CashRegisterResponseDTO> getAll();
    void updateRegister(CashRegisterRequestDTO cashRegister);
    boolean hasOpenRegisterOnDate(String username);
}
