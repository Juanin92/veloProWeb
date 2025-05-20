package com.veloProWeb.validation;

import com.veloProWeb.exceptions.supplier.SupplierNotFoundException;
import com.veloProWeb.model.entity.Purchase.Purchase;
import com.veloProWeb.model.entity.Purchase.Supplier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PurchaseValidator {

    /**
     * Válida que la compra tenga un proveedor seleccionado, lanzará una excepción
     * Si el valor de proveedor es nulo
     * @param supplier - Objeto con los datos de un proveedor
     */
    public void hasSupplier(Supplier supplier){
        if (supplier == null) {
            throw new SupplierNotFoundException("Proveedor no encontrado");
        }
    }

    /**
     * Válida el valor del total de una compra, lanzará una excepción
     * si el total es menor o igual a 0
     * @param total - cantidad a validar
     */
    public void validateTotalPurchase(int total){
        if (total <= 0) {
            throw new IllegalArgumentException("Ingrese sólo números");
        }
    }
}
