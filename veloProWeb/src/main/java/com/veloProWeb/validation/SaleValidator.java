package com.veloProWeb.validation;

import com.veloProWeb.exceptions.sale.SaleNotFoundException;
import com.veloProWeb.model.entity.Sale.Sale;
import org.springframework.stereotype.Component;

@Component
public class SaleValidator {

    public void hasSale(Sale sale){
        if (sale == null) {
            throw new SaleNotFoundException("Venta no encontrada");
        }
    }
}
