package com.veloProWeb.service.Report;

import com.veloProWeb.model.dto.KardexRequestDTO;
import com.veloProWeb.model.entity.Kardex;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.repository.KardexRepo;
import com.veloProWeb.service.product.ProductService;
import com.veloProWeb.service.User.AlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KardexServiceTest {
    @InjectMocks private KardexService kardexService;
    @Mock private KardexRepo kardexRepo;
    @Mock private ProductService productService;
    @Mock private AlertService alertService;
    private Kardex kardex, kardexExit;
    private KardexRequestDTO dto;
    private Product product;

    @BeforeEach
    void setUp(){
        kardex = new Kardex();
        kardex.setId(1L);
        kardex.setComment("Prueba");
        kardex.setDate(LocalDate.now());
        kardex.setMovementsType(MovementsType.ENTRADA);
        kardex.setStock(10);
        kardex.setPrice(2000);
        kardex.setQuantity(2);

        kardexExit = new Kardex();
        kardexExit.setId(1L);
        kardexExit.setComment("Prueba");
        kardexExit.setDate(LocalDate.now());
        kardexExit.setMovementsType(MovementsType.SALIDA);
        kardexExit.setStock(10);
        kardexExit.setPrice(2000);
        kardexExit.setQuantity(2);

        product = new Product();
        product.setId(1L);
        product.setStock(10);
        product.setBuyPrice(2000);
    }

    //Prueba para crear un registro
    @Test
    public void addKardex_valid(){
        kardex.setProduct(product);
        kardex.setId(null);
        when(productService.getProductById(1L)).thenReturn(product);
        kardexService.addKardex(product, 2, "Prueba", MovementsType.ENTRADA);

        verify(productService).getProductById(1L);
        verify(kardexRepo).save(kardex);
    }

    //Prueba para obtener todos los registros
    @Test
    public void getAll_valid(){
        when(kardexRepo.findAll()).thenReturn(Collections.singletonList(kardex));
        List<Kardex> result = kardexService.getAll();
        verify(kardexRepo).findAll();
        assertEquals(kardex.getComment(), result.getFirst().getComment());
    }

    //Prueba para validar las ventas bajas de un producto
    @Test
    public void checkLowSales_valid(){
        kardex.setProduct(product);
        kardex.setDate(LocalDate.now().minusDays(95));
        kardex.setQuantity(20);
        kardexExit.setDate(LocalDate.now().minusDays(85));
        kardexExit.setProduct(product);
        LocalDate days = LocalDate.now().minusDays(90);
        List<Kardex> kardexList = Arrays.asList(kardex, kardexExit);
        when(kardexRepo.findByProductAndDateAfter(product, days)).thenReturn(kardexList);
        when(alertService.isAlertActive(product, "Producto sin Ventas (+ 90 días), null")).thenReturn(false);
        kardexService.checkLowSales(product);

        verify(kardexRepo, times(1)).findByProductAndDateAfter(product, days);
        verify(alertService, times(1)).isAlertActive(product, "Producto sin Ventas (+ 90 días), null");
        verify(alertService, times(1)).createAlert(product, "Producto sin Ventas (+ 90 días), null");
    }
    @Test
    public void checkLowSales_noLowSalesCondition() {
        kardex.setProduct(product);
        kardex.setDate(LocalDate.now().minusDays(95));
        kardex.setQuantity(20);
        kardexExit.setDate(LocalDate.now().minusDays(85));
        kardexExit.setProduct(product);
        kardexExit.setQuantity(19);
        LocalDate days = LocalDate.now().minusDays(90);
        List<Kardex> kardexList = Arrays.asList(kardex, kardexExit);
        when(kardexRepo.findByProductAndDateAfter(product, days)).thenReturn(kardexList);
        kardexService.checkLowSales(product);

        verify(kardexRepo, times(1)).findByProductAndDateAfter(product, days);
        verify(alertService, never()).isAlertActive(any(), any());
        verify(alertService, never()).createAlert(any(), any());
    }
    @Test
    public void checkLowSales_alertAlreadyExists() {
        kardex.setProduct(product);
        kardex.setDate(LocalDate.now().minusDays(95));
        kardex.setQuantity(20);
        kardexExit.setDate(LocalDate.now().minusDays(85));
        kardexExit.setProduct(product);
        List<Kardex> kardexList = Arrays.asList(kardex, kardexExit);
        LocalDate days = LocalDate.now().minusDays(90);
        when(kardexRepo.findByProductAndDateAfter(product, days)).thenReturn(kardexList);
        when(alertService.isAlertActive(product, "Producto sin Ventas (+ 90 días), null")).thenReturn(true);

        kardexService.checkLowSales(product);

        verify(kardexRepo, times(1)).findByProductAndDateAfter(product, days);
        verify(alertService, times(1)).isAlertActive(product, "Producto sin Ventas (+ 90 días), null");
        verify(alertService, never()).createAlert(any(), any());
    }
}
