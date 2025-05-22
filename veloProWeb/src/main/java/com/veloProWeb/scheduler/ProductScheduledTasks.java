package com.veloProWeb.scheduler;

import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.repository.product.ProductRepo;
import com.veloProWeb.service.Report.IKardexService;
import com.veloProWeb.service.User.Interface.IAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProductScheduledTasks {

    private final ProductRepo productRepo;
    private final IAlertService alertService;
    private final IKardexService kardexService;

    /**
     * Verifica y crea alertas para cada producto en intervalos regulares.
     * Crea una alerta si no hay stock disponible o umbral bajo y no existe ya una alerta activa.
     * Llama al servicio de Kardex para verificar y manejar las bajas ventas del producto.
     * @Scheduled - Está programado para actuar cada 6 hr automáticamente
     */
    @Scheduled(fixedRate = 21600000)
    public void checkAndHandleProductAlerts() {
        List<Product> noStockProducts = productRepo.findOutOfStock();
        noStockProducts.forEach(product -> {
            String noStockDescription = "Sin Stock (" + product.getDescription() + " )";
            if (!alertService.isAlertActive(product, noStockDescription)){
                alertService.createAlert(product, noStockDescription);
            }
        });
        List<Product> criticalStockProducts = productRepo.findCriticalStock();
        criticalStockProducts.forEach(product -> {
            String criticalStockDescription = String.format("Stock Crítico (%s - %s unidades)",
                    product.getDescription(), product.getStock());
            if (!alertService.isAlertActive(product, criticalStockDescription) ) {
                alertService.createAlert(product, criticalStockDescription);
            }
        });
        Set<Product> allChecked = new HashSet<>();
        allChecked.addAll(noStockProducts);
        allChecked.addAll(criticalStockProducts);
        allChecked.forEach(kardexService::checkLowSales);
    }
}
