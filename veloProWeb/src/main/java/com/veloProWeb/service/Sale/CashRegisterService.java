package com.veloProWeb.service.Sale;

import com.veloProWeb.model.dto.CashRegisterDTO;
import com.veloProWeb.model.entity.Sale.CashRegister;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.model.Enum.Rol;
import com.veloProWeb.repository.Sale.CashRegisterRepo;
import com.veloProWeb.service.Sale.Interface.ICashRegisterService;
import com.veloProWeb.service.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        User user = userService.getUserByUsername(username);
        if (user.getRole() != Rol.MASTER){
            validateAmount(amount);
        }
        if (!user.getRole().equals(Rol.WAREHOUSE)) {
            CashRegister cashRegister = new CashRegister();
            cashRegister.setId(null);
            cashRegister.setDateOpening(LocalDateTime.now());
            cashRegister.setDateClosing(null);
            cashRegister.setAmountOpening(amount);
            cashRegister.setAmountClosingCash(0);
            cashRegister.setAmountClosingPos(0);
            cashRegister.setStatus("OPEN");
            cashRegister.setComment(null);
            cashRegister.setAlert(false);
            cashRegister.setUser(user);
            cashRegisterRepo.save(cashRegister);
        }
    }

    /**
     * Agrega los datos de cierre al registro de caja realizado por un usuario
     * Valida los montos ingresados.
     * Verifica el usuario, si esta presenta agregas los datos y deja cerrado el registro
     * @param dto - Objeto con los datos necesario
     */
    @Override
    public void addRegisterClosing(String username, CashRegisterDTO dto) {
        User user = userService.getUserByUsername(username);
        if (user.getRole() != Rol.MASTER){
            validateAmount(dto.getAmountClosingCash());
            validateAmount(dto.getAmountClosingPos());
        }
        if (!user.getRole().equals(Rol.WAREHOUSE)) {
            CashRegister cashRegister = getOpeningRegisterByUser(user.getId());
            cashRegister.setAmountClosingCash(dto.getAmountClosingCash());
            cashRegister.setAmountClosingPos(dto.getAmountClosingPos());
            cashRegister.setDateClosing(LocalDateTime.now());
            cashRegister.setStatus("CLOSED");
            cashRegister.setComment(dto.getComment());
            if (dto.getComment().toLowerCase().matches(".*\\berror\\b.*")) {
                cashRegister.setAlert(true);
            }
            cashRegisterRepo.save(cashRegister);
            validateClosingAndOpeningAmount(cashRegister);
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
                    cashRegister.getStatus(), cashRegister.getComment(), cashRegister.isAlert(),
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
     * Verificar si un usuario tiene un registro de caja abierto en la fecha actual
     * @param username - Nombre de usuario
     * @return - Verdadero si el usuario tiene un registro abierto en la fecha actual
     */
    @Override
    public boolean hasOpenRegisterOnDate(String username) {
        User user = userService.getUserByUsername(username);
        if (user != null) {
            LocalDate date = LocalDate.now();
            return cashRegisterRepo.existsOpenRegisterByUserAndDate(user.getId(), date);
        }
        return false;
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

    /**
     * Validar diferencias entre el monto de cierre y apertura de caja,
     * si el monto de cierre es menor al de apertura se agrega un comentario
     * y se marca como alerta
     * @param cashRegister - Objeto del registro de caja
     */
    private void validateClosingAndOpeningAmount(CashRegister cashRegister) {
        if (cashRegister.getAmountClosingCash() < cashRegister.getAmountOpening()) {
            cashRegister.setComment(cashRegister.getComment() +
                    " - El monto de cierre en efectivo es menor a la apertura.");
            cashRegister.setAlert(true);
            cashRegisterRepo.save(cashRegister);
        }
    }
}
