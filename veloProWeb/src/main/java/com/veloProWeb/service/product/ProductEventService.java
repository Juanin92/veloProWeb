package com.veloProWeb.service.product;

import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.model.dto.product.ProductUpdatedRequestDTO;
import com.veloProWeb.model.entity.Kardex;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.service.Report.IKardexService;
import com.veloProWeb.service.User.Interface.IAlertService;
import com.veloProWeb.service.product.interfaces.IProductEventService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductEventService implements IProductEventService {

    private final IAlertService alertService;
    private final IKardexService kardexService;

    /**
     * Registra la creación inicial de un producto en el kardex con tipo de movimiento AJUSTE.
     * @param product - Producto registrado.
     * @param comment - Comentario sobre el registro.
     * @param userDetails - Información del usuario que realiza la acción.
     */
    @Override
    public void handleCreateRegister(Product product, String comment, UserDetails userDetails){
        kardexService.addKardex(userDetails, product, 0, comment, MovementsType.AJUSTE);
    }

    /**
     * Válida si el stock fue modificado y en ese caso, registra un ajuste en el kardex
     * y genera una alerta.
     * @param product - Producto actualizado.
     * @param originalStock - Stock original antes de la actualización.
     * @param dto - DTO con información del nuevo stock y comentario.
     * @param userDetails - Información del usuario que realiza la acción.
     */
    @Override
    public void isChangeStockOriginalValue(Product product, int originalStock, ProductUpdatedRequestDTO dto,
                                           UserDetails userDetails) {
        if (originalStock != dto.getStock()) {
            String comment = String.format("%s - stock original: %s, stock nuevo: %s", dto.getComment(),
                    originalStock, dto.getStock());
            int quantity = Math.abs(originalStock - dto.getStock());
            kardexService.addKardex(userDetails, product, quantity, comment, MovementsType.AJUSTE);
            alertService.createAlert(product, comment);
        }
    }

    /**
     * Genera una alerta si el producto no tiene stock y no existe una alerta activa.
     * @param product - Producto sin stock.
     */
    @Override
    public void handleNoStockAlert(Product product) {
        String description = String.format("Sin Stock ( %s )", product.getDescription());
        if (!alertService.isAlertActive(product, description)) {
            alertService.createAlert(product, description);
        }
    }

    /**
     * Genera una alerta si el stock del producto es crítico y no existe una alerta activa.
     * @param product - Producto con stock crítico.
     */
    @Override
    public void handleCriticalStockAlert(Product product) {
        String description = String.format("Stock Crítico (%s - %s unidades)", product.getDescription(),
                product.getStock());
        if (!alertService.isAlertActive(product, description)) {
            alertService.createAlert(product, description);
        }
    }

    /**
     * Revisa si el producto ha tenido pocas ventas en los últimos 90 días
     * y genera una alerta si corresponde.
     * @param product - Producto a evaluar.
     */
    @Override
    public void checkLowSales(Product product) {
        LocalDate startDate = LocalDate.now().minusDays(90);
        List<Kardex> kardexList = kardexService.getProductMovementsSinceDate(product, startDate);

        Optional<Kardex> lastEntry = kardexList.stream()
                .filter(kardex -> kardex.getMovementsType() == MovementsType.ENTRADA)
                .max(Comparator.comparing(Kardex::getDate));

        Optional<Kardex> lastExit = kardexList.stream()
                .filter(kardex -> kardex.getMovementsType() == MovementsType.SALIDA)
                .max(Comparator.comparing(Kardex::getDate));

        int totalEntries = lastEntry.map(Kardex::getQuantity).orElse(0);
        int totalExits = lastExit.map(Kardex::getQuantity).orElse(0);
        int difference = totalEntries - totalExits;
        String description = String.format("Producto sin Ventas (+ 90 días) -> %s", product.getDescription());
        if (difference > (totalEntries / 2) && !alertService.isAlertActive(product, description)){
            alertService.createAlert(product, description);
        }
    }
}
