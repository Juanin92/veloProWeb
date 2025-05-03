package com.veloProWeb.validation;

import com.veloProWeb.model.entity.Purchase.Purchase;
import com.veloProWeb.model.entity.Purchase.Supplier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PurchaseValidator {

    /**
     * Válida los datos de una compra
     * @param purchase - Objeto con los valores de una compra
     */
    public void validate(Purchase purchase){
        validateDate(purchase.getDate());
        validateSupplier(purchase.getSupplier());
        validateDocument(purchase.getDocument());
        validateTotal(purchase.getPurchaseTotal());
    }

    /**
     * Válida que la compra tenga una fecha, lanzará excepción
     * Si la fecha es nula
     * @param date - Formato de fecha a validar
     */
    private void validateDate(LocalDate date){
        if (date == null){
            throw new IllegalArgumentException("Ingrese una fecha");
        }
    }

    /**
     * Válida que la compra tenga un proveedor seleccionado, lanzará una excepción
     * Si el valor de proveedor es nulo
     * @param supplier - Objeto con los datos de un proveedor
     */
    private void validateSupplier(Supplier supplier){
        if (supplier == null) {
            throw new IllegalArgumentException("Seleccione un proveedor");
        }
    }

    /**
     * Válida que la compra posea un documento, lanzará excepción
     * Si el documento es nulo o si la cadena esta vaciá
     * @param document - cadena que contiene el valor del documento
     */
    private void validateDocument(String document){
        if (document == null || document.trim().isBlank()) {
            throw new IllegalArgumentException("Ingrese número de documento");
        }
    }

    /**
     * Válida el valor del total de una compra, lanzará una excepción
     * si el total es menor o igual a 0
     * @param total - cantidad a validar
     */
    private void validateTotal(int total){
        if (total <= 0) {
            throw new IllegalArgumentException("Ingrese sólo números");
        }
    }
}
