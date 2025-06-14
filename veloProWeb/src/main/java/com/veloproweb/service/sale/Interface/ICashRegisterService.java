package com.veloproweb.service.sale.Interface;

import com.veloproweb.model.dto.sale.CashRegisterRequestDTO;
import com.veloproweb.model.dto.sale.CashRegisterResponseDTO;

import java.util.List;

public interface ICashRegisterService {
    void openRegister(String username, int amount);
    void closeRegister(String username, CashRegisterRequestDTO cashRegister);
    List<CashRegisterResponseDTO> getCashRegisters();
    void updateRegister(CashRegisterRequestDTO cashRegister);
    boolean hasOpenRegisterOnDate(String username);
}
