package com.veloProWeb.validation;

import com.veloProWeb.exceptions.purchase.PurchaseNotFoundException;
import com.veloProWeb.exceptions.supplier.SupplierNotFoundException;
import com.veloProWeb.model.entity.Purchase.Purchase;
import com.veloProWeb.model.entity.Purchase.Supplier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PurchaseValidator {

    /**
     * Válida que la compra tenga un proveedor seleccionado, lanzará una excepción
     * @param supplier - Objeto con los datos de un proveedor
     */
    public void hasSupplier(Supplier supplier){
        if (supplier == null) {
            throw new SupplierNotFoundException("Proveedor no encontrado");
        }
    }

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
