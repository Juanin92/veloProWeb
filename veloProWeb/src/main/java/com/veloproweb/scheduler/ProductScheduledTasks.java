package com.veloproweb.scheduler;

import com.veloproweb.model.entity.product.Product;
import com.veloproweb.repository.product.ProductRepo;
import com.veloproweb.service.product.interfaces.IProductEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ProductScheduledTasks {

    private final ProductRepo productRepo;
    private final IProductEventService productEventService;

    /**
     * Verifica y crea alertas para cada producto en intervalos regulares.
     * Crea una alerta si no hay stock disponible o umbral bajo y no existe ya una alerta activa.
     * Llama al servicio de Kardex para verificar y manejar las bajas ventas del producto.
     * @Scheduled - Está programado para actuar cada 6 hr automáticamente
     */
    @Scheduled(fixedRate = 21600000)
    @Async("taskExecutor")
    public void checkAndHandleProductAlerts() {
        List<Product> noStockProducts = productRepo.findOutOfStock();
        noStockProducts.forEach(productEventService::handleNoStockAlert);

        List<Product> criticalStockProducts = productRepo.findCriticalStock();
        criticalStockProducts.forEach(productEventService::handleCriticalStockAlert);

        Set<Product> allChecked = new HashSet<>();
        allChecked.addAll(noStockProducts);
        allChecked.addAll(criticalStockProducts);
        allChecked.forEach(productEventService::checkLowSales);
    }
}
