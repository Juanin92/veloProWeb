package com.veloproweb.validation;

import com.veloproweb.exceptions.sale.SaleNotFoundException;
import com.veloproweb.model.entity.sale.Sale;
import org.springframework.stereotype.Component;

@Component
public class SaleValidator {

    public void hasSale(Sale sale){
        if (sale == null) {
            throw new SaleNotFoundException("Venta no encontrada");
        }
    }
}
