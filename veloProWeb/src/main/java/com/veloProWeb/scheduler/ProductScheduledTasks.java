package com.veloProWeb.scheduler;

import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.repository.product.ProductRepo;
import com.veloProWeb.service.Report.IkardexService;
import com.veloProWeb.service.User.Interface.IAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductScheduledTasks {

    private final ProductRepo productRepo;
    private final IAlertService alertService;
    private final IkardexService kardexService;

    /**
     * Verifica y crea alertas para cada producto en intervalos regulares.
     * Crea una alerta si no hay stock disponible o umbral bajo y no existe ya una alerta activa.
     * Llama al servicio de Kardex para verificar y manejar las bajas ventas del producto.
     * @Scheduled - Está programado para actuar cada 6 hr automáticamente
     */
    @Scheduled(fixedRate = 21600000)
    public void checkAndCreateAlertsByProduct() {
        List<Product> products =  productRepo.findAll();
        for (Product product : products){
            String noStockDescription = "Sin Stock (" + product.getDescription() + " )";
            if (product.getStock() == 0 && !alertService.isAlertActive(product, noStockDescription)){
                alertService.createAlert(product, noStockDescription);
            }
            String criticalStockDescription = "Stock Crítico (" + product.getDescription() + " - " + product.getStock() + " unidades)";
            if (product.getStock() < product.getThreshold() && !alertService.isAlertActive(product, criticalStockDescription) ) {
                alertService.createAlert(product, criticalStockDescription);
            }
            kardexService.checkLowSales(product);
        }
    }
}
