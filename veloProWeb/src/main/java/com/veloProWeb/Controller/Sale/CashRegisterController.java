package com.veloProWeb.Controller.Sale;

import com.veloProWeb.Model.Entity.Sale.CashRegister;
import com.veloProWeb.Service.Record.IRecordService;
import com.veloProWeb.Service.Sale.Interface.ICashRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para gestionar operaciones relacionadas con registro de caja.
 * Este controlador proporciona endpoints para listar registro de caja.
 */
@RestController
@RequestMapping("/caja")
@CrossOrigin(origins = "http://localhost:4200")
public class CashRegisterController {

    @Autowired private ICashRegisterService cashRegisterService;
    @Autowired private IRecordService recordService;

    /**
     * Obtiene una lista con los registros de caja
     * @return - lista con los registros
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER')")
    public List<CashRegister> getCashRegisters(@AuthenticationPrincipal UserDetails userDetails){
        recordService.registerAction(userDetails, "VIEW", "Lista de registro de la caja");
        return cashRegisterService.getAll();
    }
}
