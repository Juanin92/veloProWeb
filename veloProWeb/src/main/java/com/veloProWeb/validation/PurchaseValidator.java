package com.veloProWeb.validation;

import com.veloProWeb.exceptions.purchase.PurchaseNotFoundException;
import com.veloProWeb.model.entity.purchase.Purchase;
import org.springframework.stereotype.Component;

@Component
public class PurchaseValidator {

    /**
     * Válida que exista una compra seleccionada, lanzará una excepción
     * @param purchase - Objeto con los datos de un proveedor
     */
    public void hasPurchase(Purchase purchase){
        if (purchase == null) {
            throw new PurchaseNotFoundException("Compra no encontrada");
        }
    }
}
