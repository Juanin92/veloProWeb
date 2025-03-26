package com.veloProWeb.Service.Sale;

import com.veloProWeb.Model.DTO.CashRegisterDTO;
import com.veloProWeb.Model.Entity.Sale.CashRegister;
import com.veloProWeb.Model.Entity.User.User;
import com.veloProWeb.Model.Enum.Rol;
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

    /**
     * Crea un registro de caja con los de la apertura
     * @param username - nombre de usuario del usuario que hace el registro
     * @param amount - monto de la apertura de caja
     */
    @Override
    public void addRegisterOpening(String username, int amount) {
        User user = userService.getUserWithUsername(username);
        validateAmount(amount);
        if (user != null) {
            if (!user.getRole().equals(Rol.WAREHOUSE)){
                CashRegister cashRegister = new CashRegister();
                cashRegister.setId(null);
                cashRegister.setDateOpening(LocalDateTime.now());
                cashRegister.setDateClosing(null);
                cashRegister.setAmountOpening(amount);
                cashRegister.setAmountClosingCash(0);
                cashRegister.setAmountClosingPos(0);
                cashRegister.setStatus("OPEN");
                cashRegister.setComment(null);
                cashRegister.setUser(user);
                cashRegisterRepo.save(cashRegister);
            }
        }
    }

    /**
     * Agrega los datos de cierre al registro de caja realizado por un usuario
     * Valida los montos ingresados.
     * Verifica el usuario, si esta presenta agregas los datos y deja cerrado el registro
     * @param dto - Objeto con los datos necesario
     */
    @Override
    public void addRegisterClosing(CashRegisterDTO dto) {
        User user = userService.getUserWithUsername(dto.getUser());
        validateAmount(dto.getAmountClosingCash());
        validateAmount(dto.getAmountClosingPos());
        if (user != null) {
            if(!user.getRole().equals(Rol.WAREHOUSE)){
                CashRegister cashRegister = getOpeningRegisterByUser(user.getId());
                if (dto.getAmountClosingCash() < cashRegister.getAmountOpening()) {
                    throw new IllegalArgumentException("El monto de cierre en efectivo es menor a la apertura.");
                }
                cashRegister.setAmountClosingCash(dto.getAmountClosingCash());
                cashRegister.setAmountClosingPos(dto.getAmountClosingPos());
                cashRegister.setDateClosing(LocalDateTime.now());
                cashRegister.setStatus("CLOSED");
                cashRegister.setComment(null);
                cashRegisterRepo.save(cashRegister);
            }
        }
    }

    /**
     * Agrega un comentario a un registro de caja por su ID ya creado.
     * Verifica que el comentario tenga un valor.
     * Guarda el comentario si se encuentra el registro correspondiente
     * @param dto - Objeto con los datos necesario
     */
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

    /**
     * Actualizar los montos de un registro seleccionado.
     * Buscar el registro a actualizar y si está presente se actualiza
     * @param dto - Datos a actualizar del registro seleccionado
     */
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

    /**
     * Obtener un registro hecho por un usuario específico.
     * Verifica si el registro esta vació o la fecha es después de la fecha de apertura
     * @param userID - Identificador del usuario
     * @return - Objeto del registro de caja encontrado
     */
    private CashRegister getOpeningRegisterByUser(Long userID) {
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
     * Validar los montos ingresados
     * @param amount - Monto a validar
     */
    private void validateAmount(int amount){
        if (amount <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
    }
}
