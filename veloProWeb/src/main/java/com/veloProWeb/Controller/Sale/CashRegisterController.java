package com.veloProWeb.Controller.Sale;

import com.veloProWeb.Model.Entity.Sale.CashRegister;
import com.veloProWeb.Service.Sale.CashRegisterService;
import com.veloProWeb.Service.Sale.Interface.ICashRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * Obtiene una lista con los registros de caja
     * @return - lista con los registros
     */
    @GetMapping
    public List<CashRegister> getCashRegisters(){
        return cashRegisterService.getAll();
    }
}
