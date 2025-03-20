package com.veloProWeb.Service.Sale;

import com.veloProWeb.Model.DTO.CashRegisterDTO;
import com.veloProWeb.Model.Entity.Sale.CashRegister;
import com.veloProWeb.Model.Entity.User.User;
import com.veloProWeb.Repository.Sale.CashRegisterRepo;
import com.veloProWeb.Service.Sale.Interface.ICashRegisterService;
import com.veloProWeb.Service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CashRegisterService implements ICashRegisterService {

    @Autowired private CashRegisterRepo cashRegisterRepo;
    @Autowired private UserService userService;

    @Override
    public void addRegisterOpening(String username, int amount) {
        User user = userService.getUserWithUsername(username);
        validateAmount(amount);
        if (user != null) {
            CashRegister cashRegister = new CashRegister();
            cashRegister.setId(null);
            cashRegister.setDateOpening(LocalDateTime.now());
            cashRegister.setDateClosing(null);
            cashRegister.setAmountOpening(amount);
            cashRegister.setAmountClosingCash(0);
            cashRegister.setAmountClosingPos(0);
            cashRegister.setStatus("OPEN");
            cashRegister.setComment(null);
            cashRegisterRepo.save(cashRegister);
        }
    }

    @Override
    public void addRegisterClosing(CashRegisterDTO dto) {
        User user = userService.getUserWithUsername(dto.getUser());
        validateAmount(dto.getAmountClosingCash());
        validateAmount(dto.getAmountClosingPos());
        if (user != null) {
            CashRegister cashRegister = getRegisterByUser(user.getId());
            if (cashRegister.getAmountClosingCash() < cashRegister.getAmountOpening()) {
                throw new IllegalArgumentException("El monto de cierre en efectivo es menor a la apertura.");
            }
            cashRegister.setAmountClosingCash(cashRegister.getAmountClosingCash());
            cashRegister.setAmountClosingPos(cashRegister.getAmountClosingPos());
            cashRegister.setDateClosing(LocalDateTime.now());
            cashRegister.setStatus("CLOSED");
            cashRegister.setComment(null);
            cashRegisterRepo.save(cashRegister);
        }
    }

    @Override
    public void addRegisterValidateComment(CashRegisterDTO dto) {
        if (dto.getComment() == null || dto.getComment().trim().isEmpty()){
            throw new IllegalArgumentException("Debes agregar un comentario para registrar.");
        }
        Optional<CashRegister> cashRegister = cashRegisterRepo.findById(dto.getId());
        if (cashRegister.isPresent()) {
            cashRegister.get().setComment(dto.getComment());
            cashRegisterRepo.save(cashRegister.get());
        }
    }

    @Override
    public CashRegister getRegisterByUser(Long userID) {
        CashRegister cashRegister = cashRegisterRepo.findLatestOpenRegisterByUser(userID);
        if (cashRegister == null) {
            throw new IllegalArgumentException("No hay registro de apertura válido.");
        }
        if (cashRegister.getDateOpening().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha no coincide con la apertura.");
        }
        return cashRegister;
    }

    /**
     * Obtener una lista de registro de la caja
     * @return - lista con los registros
     */
    @Override
    public List<CashRegisterDTO> getAll() {
        List<CashRegister> cashRegisters= cashRegisterRepo.findAll();
        return cashRegisters.stream().map(cashRegister ->
            new CashRegisterDTO(cashRegister.getId(), cashRegister.getDateOpening(), cashRegister.getDateClosing(),
                    cashRegister.getAmountOpening(), cashRegister.getAmountClosingCash(), cashRegister.getAmountClosingPos(),
                    cashRegister.getStatus(), cashRegister.getComment(),
                    String.format("%s %s", cashRegister.getUser().getName(), cashRegister.getUser().getSurname()))
        ).toList();
    }

    @Override
    public void updateRegister(CashRegisterDTO dto) {
        Optional<CashRegister> cashRegister = cashRegisterRepo.findById(dto.getId());
        if (cashRegister.isPresent()) {
            cashRegister.get().setAmountOpening(dto.getAmountOpening());
            cashRegister.get().setAmountClosingPos(dto.getAmountClosingPos());
            cashRegister.get().setAmountClosingCash(dto.getAmountClosingCash());
            cashRegisterRepo.save(cashRegister.get());
        }
    }

    private void validateAmount(int amount){
        if (amount <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
    }
}
