package com.veloProWeb.service.sale;

import com.veloProWeb.exceptions.sale.CashRegisterNotFoundException;
import com.veloProWeb.mapper.CashRegisterMapper;
import com.veloProWeb.model.dto.sale.CashRegisterRequestDTO;
import com.veloProWeb.model.dto.sale.CashRegisterResponseDTO;
import com.veloProWeb.model.entity.Sale.CashRegister;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.model.Enum.Rol;
import com.veloProWeb.repository.Sale.CashRegisterRepo;
import com.veloProWeb.repository.UserRepo;
import com.veloProWeb.service.sale.Interface.ICashRegisterService;
import com.veloProWeb.service.user.interfaces.IUserService;
import com.veloProWeb.validation.CashRegisterValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CashRegisterService implements ICashRegisterService {

    private final CashRegisterRepo cashRegisterRepo;
    private final UserRepo userRepo;
    private final IUserService userService;
    private final CashRegisterMapper mapper;
    private final CashRegisterValidator validator;

    /**
     * Crea un registro de caja con los de la apertura
     * @param username - nombre de usuario del usuario que hace el registro
     * @param amount - monto de la apertura de caja
     */
    @Transactional
    @Override
    public void openRegister(String username, int amount) {
        User user = userService.getUserByUsername(username);
        validator.validateRoleCanRegister(user.getRole().equals(Rol.WAREHOUSE));
        validator.validateAmount(amount);
        CashRegister cashRegister = mapper.toOpeningRegisterEntity(user, amount);
        cashRegisterRepo.save(cashRegister);
    }

    /**
     * Agrega los datos de cierre al registro de caja realizado por un usuario
     * Valida los montos ingresados.
     * Verifica el usuario, si esta presenta agregas los datos y deja cerrado el registro
     * @param dto - Objeto con los datos necesario
     */
    @Transactional
    @Override
    public void closeRegister(String username, CashRegisterRequestDTO dto) {
        User user = userService.getUserByUsername(username);
        validator.validateRoleCanRegister(user.getRole().equals(Rol.WAREHOUSE));
        CashRegister cashRegister = cashRegisterRepo.findLatestOpenRegisterByUser(user.getId());
        validator.validateCloseRegister(dto, cashRegister);

        cashRegister.setDateClosing(LocalDateTime.now());
        cashRegister.setAmountClosingCash(dto.getAmountClosingCash());
        cashRegister.setAmountClosingPos(dto.getAmountClosingPos());
        cashRegister.setComment(dto.getComment());
        cashRegister.setStatus("CLOSED");
        if (dto.getComment().toLowerCase().matches(".*\\berror\\b.*")) {
            cashRegister.setAlert(true);
        }
        cashRegisterRepo.save(cashRegister);
        checkForNegativeClosingDifference(cashRegister);
    }

    /**
     * Obtener una lista de registro de la caja
     * @return - lista con los registros
     */
    @Override
    public List<CashRegisterResponseDTO> getCashRegisters() {
        List<CashRegister> cashRegisters= cashRegisterRepo.findAll();
        return cashRegisters.stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    /**
     * Actualizar los montos de un registro seleccionado.
     * Buscar el registro a actualizar y si está presente se actualiza
     * @param dto - Datos a actualizar del registro seleccionado
     */
    @Transactional
    @Override
    public void updateRegister(CashRegisterRequestDTO dto) {
        CashRegister cashRegister = cashRegisterRepo.findById(dto.getId()).orElseThrow(
                () -> new CashRegisterNotFoundException("Registro de caja no encontrado"));
        cashRegister.setAmountOpening(dto.getAmountOpening());
        cashRegister.setAmountClosingPos(dto.getAmountClosingPos());
        cashRegister.setAmountClosingCash(dto.getAmountClosingCash());
        cashRegisterRepo.save(cashRegister);
    }

    /**
     * Verificar si un usuario tiene un registro de caja abierto en la fecha actual
     * @param username - Nombre de usuario
     * @return - Verdadero si el usuario tiene un registro abierto en la fecha actual
     */
    @Override
    public boolean hasOpenRegisterOnDate(String username) {
        Optional<User> user = userRepo.findByUsername(username);
        if (user.isPresent()) {
            LocalDate date = LocalDate.now();
            return cashRegisterRepo.existsOpenRegisterByUserAndDate(user.get().getId(), date);
        }
        return false;
    }

    /**
     * Validar diferencias entre el monto de cierre y apertura de caja,
     * si el monto de cierre es menor al de apertura se agrega un comentario
     * y se marca como alerta
     * @param cashRegister - Objeto del registro de caja
     */
    private void checkForNegativeClosingDifference(CashRegister cashRegister) {
        if (cashRegister.getAmountClosingCash() < cashRegister.getAmountOpening()) {
            cashRegister.setComment(String.format("%s - El monto de cierre en efectivo es menor a la apertura.",
                    cashRegister.getComment()));
            cashRegister.setAlert(true);
            cashRegisterRepo.save(cashRegister);
        }
    }
}
