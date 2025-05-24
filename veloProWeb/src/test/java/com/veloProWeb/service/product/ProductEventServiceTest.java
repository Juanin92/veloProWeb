package com.veloProWeb.service.product;

import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.model.dto.product.ProductUpdatedRequestDTO;
import com.veloProWeb.model.entity.Kardex;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.service.report.IKardexService;
import com.veloProWeb.service.User.Interface.IAlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductEventServiceTest {

    @InjectMocks private ProductEventService productEventService;
    @Mock private IKardexService kardexService;
    @Mock private IAlertService alertService;
    @Mock private UserDetails userDetails;
    private Product product;
    private ProductUpdatedRequestDTO dto;

    @BeforeEach
    void setUp(){
        product = Product.builder().id(1L).description("TV").stock(10).salePrice(1000).buyPrice(100).build();
        dto = ProductUpdatedRequestDTO.builder().id(1L).description("TV").stock(11).salePrice(1000)
                .comment("Test Comment").build();
    }

    //Prueba para el manejo de creación de un registro de un kardex
    @Test
    public void handleCreateRegister_valid(){
        productEventService.handleCreateRegister(product, "Test comment", userDetails);
        verify(kardexService, times(1)).addKardex(userDetails, product, 0,
                "Test comment", MovementsType.AJUSTE);
    }

    //Prueba para cuando el cambio de stock ha cambiado
    @Test
    public void isChangeStockOriginalValue_valid(){
        productEventService.isChangeStockOriginalValue(product, 10, dto, userDetails);
        String comment = String.format("%s - stock original: %s, stock nuevo: %s", dto.getComment(),
                10, dto.getStock());
        int quantity = Math.abs(10 - dto.getStock());

        verify(kardexService, times(1)).addKardex(userDetails, product, quantity,
                comment, MovementsType.AJUSTE);
        verify(alertService, times(1)).createAlert(product, comment);
    }
    @Test
    public void isChangeStockOriginalValue_NoChangeStock(){
        dto.setStock(10);
        productEventService.isChangeStockOriginalValue(product, 10, dto, userDetails);

        verifyNoInteractions(kardexService, alertService);
    }

    //Prueba para manejar cuando hay alerta de producto sin stock
    @Test
    public void handleNoStockAlert_valid(){
        String description = String.format("Sin Stock ( %s )", product.getDescription());
        when(alertService.isAlertActive(product, description)).thenReturn(false);

        productEventService.handleNoStockAlert(product);

        verify(alertService, times(1)).isAlertActive(product, description);
        verify(alertService, times(1)).createAlert(product, description);
    }
    @Test
    public void handleNoStockAlert_alert(){
        String description = String.format("Sin Stock ( %s )", product.getDescription());
        when(alertService.isAlertActive(product, description)).thenReturn(true);

        productEventService.handleNoStockAlert(product);

        verify(alertService, times(1)).isAlertActive(product, description);
        verify(alertService, never()).createAlert(product, description);
    }

    //Prueba para manejar cuando hay alerta de stock crítico de un producto
    @Test
    public void handleCriticalStockAlert_valid(){
        String description = String.format("Stock Crítico (%s - %s unidades)", product.getDescription(),
                product.getStock());
        when(alertService.isAlertActive(product, description)).thenReturn(false);

        productEventService.handleCriticalStockAlert(product);

        verify(alertService, times(1)).isAlertActive(product, description);
        verify(alertService, times(1)).createAlert(product, description);
    }
    @Test
    public void handleCriticalStockAlert_alert(){
        String description = String.format("Stock Crítico (%s - %s unidades)", product.getDescription(),
                product.getStock());
        when(alertService.isAlertActive(product, description)).thenReturn(true);

        productEventService.handleCriticalStockAlert(product);

        verify(alertService, times(1)).isAlertActive(product, description);
        verify(alertService, never()).createAlert(product, description);
    }

    //Prueba para validar las ventas bajas de un producto
    @Test
    public void checkLowSales_valid(){
        LocalDate days = LocalDate.now().minusDays(90);
        Product product = Product.builder().id(1L).description("Product test").stock(10).buyPrice(2000).build();
        Kardex kardexEntry = Kardex.builder().id(1L).product(product).quantity(20).movementsType(MovementsType.ENTRADA)
                .date(LocalDate.now().minusDays(95)).stock(10).price(2000).build();
        Kardex kardexExit = Kardex.builder().id(1L).product(product).quantity(2).movementsType(MovementsType.SALIDA)
                .date(LocalDate.now().minusDays(85)).stock(19).price(2000).build();
        when(kardexService.getProductMovementsSinceDate(product, days)).thenReturn(List.of(kardexEntry, kardexExit));
        String description = String.format("Producto sin Ventas (+ 90 días) -> %s", product.getDescription());
        when(alertService.isAlertActive(product, description)).thenReturn(false);

        productEventService.checkLowSales(product);

        verify(kardexService, times(1)).getProductMovementsSinceDate(product, days);
        verify(alertService, times(1)).isAlertActive(product, description);
        verify(alertService, times(1)).createAlert(product, description);
    }
    @Test
    public void checkLowSales_noLowSalesCondition() {
        LocalDate days = LocalDate.now().minusDays(90);
        Product product = Product.builder().build();
        Kardex kardexEntry = Kardex.builder().id(1L).product(product).quantity(20).movementsType(MovementsType.ENTRADA)
                .date(LocalDate.now().minusDays(95)).stock(10).price(2000).build();
        Kardex kardexExit = Kardex.builder().id(1L).product(product).quantity(19).movementsType(MovementsType.SALIDA)
                .date(LocalDate.now().minusDays(85)).stock(19).price(2000).build();
        when(kardexService.getProductMovementsSinceDate(product, days)).thenReturn(List.of(kardexEntry, kardexExit));

        productEventService.checkLowSales(product);

        verify(kardexService, times(1)).getProductMovementsSinceDate(product, days);
        verifyNoInteractions(alertService);
    }
    @Test
    public void checkLowSales_alertAlreadyExists() {
        LocalDate days = LocalDate.now().minusDays(90);
        Product product = Product.builder().build();
        Kardex kardexEntry = Kardex.builder().id(1L).product(product).quantity(20).movementsType(MovementsType.ENTRADA)
                .date(LocalDate.now().minusDays(95)).stock(10).price(2000).build();
        Kardex kardexExit = Kardex.builder().id(1L).product(product).quantity(2).movementsType(MovementsType.SALIDA)
                .date(LocalDate.now().minusDays(85)).stock(10).price(2000).build();
        String description = String.format("Producto sin Ventas (+ 90 días) -> %s", product.getDescription());
        when(kardexService.getProductMovementsSinceDate(product, days)).thenReturn(List.of(kardexEntry, kardexExit));
        when(alertService.isAlertActive(product, description)).thenReturn(true);

        productEventService.checkLowSales(product);

        verify(kardexService, times(1)).getProductMovementsSinceDate(product, days);
        verify(alertService, times(1)).isAlertActive(product, description);
        verify(alertService, never()).createAlert(any(), any());
    }
}
