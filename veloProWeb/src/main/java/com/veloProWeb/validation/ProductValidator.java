package com.veloProWeb.validation;

import com.veloProWeb.exceptions.product.ProductAlreadyActivatedException;
import com.veloProWeb.exceptions.product.ProductAlreadyDeletedException;
import com.veloProWeb.model.entity.Product.*;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    /**
     * Válida el monto del stock, lanzará una excepción:
     * Si el monto es menor o igual a 0
     * @param stock - valor del monto
     */
    public void validateStock(int stock){
        if (stock <= 0){
            throw new IllegalArgumentException("Cantidad de ser mayor a 0");
        }
    }

    /**
     * Válida el monto del umbral, lanzará una excepción:
     * Si el monto es menor o igual a 0
     * @param number - valor del monto
     */
    public void validateThreshold(int number){
        if (number <= 0){
            throw new IllegalArgumentException("Cantidad de ser mayor a 0");
        }
    }

    /**
     * Válida el precio de venta, lanzará una excepción:
     * Si el monto es menor o igual 0
     * @param salePrice - valor del monto
     */
    public void validateSalePrice(int salePrice){
        if (salePrice <= 0){
            throw new IllegalArgumentException("Precio de ser mayor a 0");
        }
    }

    /**
     * Válida si el producto está activado
     * @param product - producto a validar
     */
    public void isActivated(Product product){
        if (product.isStatus()) {
            throw new ProductAlreadyActivatedException("El producto ya está activado.");
        }
    }

    /**
     * Válida si el producto está desactivado
     * @param product - Producto a validar
     */
    public void isDeleted(Product product){
        if (!product.isStatus()) {
            throw new ProductAlreadyDeletedException("El producto ya está desactivado.");
        }
    }

    public boolean isChangeStockOriginalValue(Product product, int newStock){
        return product.getStock() != newStock;
    }
}
