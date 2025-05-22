package com.veloProWeb.validation;

import com.veloProWeb.exceptions.purchase.PurchaseNotFoundException;
import com.veloProWeb.exceptions.purchase.PurchaseTotalMismatchException;
import com.veloProWeb.model.dto.purchase.PurchaseDetailRequestDTO;
import com.veloProWeb.model.entity.purchase.Purchase;
import org.springframework.stereotype.Component;

import java.util.List;

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

    /**
     * Válida total de la compra y la suma del detalle coincidan
     * @param purchaseTotal - monto total de la compra
     * @param detailList - lista con los detalle de la compra
     */
    public void validateTotal(int purchaseTotal, List<PurchaseDetailRequestDTO> detailList){
        int detailsTotal = detailList.stream()
                .mapToInt(PurchaseDetailRequestDTO::getTotal)
                .sum();
        if (purchaseTotal != detailsTotal){
            throw new PurchaseTotalMismatchException(String.format("""
                    Valor de total de compra no coinciden \
                    
                    total compra: %s\s
                    total de detalle: %s""", purchaseTotal, detailsTotal));
        }
    }
}
