package com.veloProWeb.Service.Product;

import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Enum.StatusProduct;
import com.veloProWeb.Repository.Product.ProductRepo;
import com.veloProWeb.Service.Report.IkardexService;
import com.veloProWeb.Service.User.Interface.IAlertService;
import com.veloProWeb.Validation.ProductValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks private ProductService productService;
    @Mock private ProductRepo productRepo;
    @Mock private ProductValidator validator;
    @Mock private IAlertService alertService;
    @Mock private IkardexService kardexService;
    private Product product;

    @BeforeEach
    void setUp(){
        product = new Product();
    }

    //Prueba para crear un nuevo producto
    @Test
    public void create_valid(){
        productService.create(product);

        verify(validator).validateNewProduct(product);
        verify(productRepo).save(product);
        assertFalse(product.isStatus());
        assertEquals(StatusProduct.NODISPONIBLE, product.getStatusProduct());
        assertEquals(0, product.getBuyPrice());
        assertEquals(0, product.getSalePrice());
        assertEquals(0, product.getStock());
    }

    //Prueba para actualizar un producto
    @Test
    public void update_validMoreStock(){
        product.setStock(10);
        productService.update(product);

        verify(productRepo).save(product);
        assertTrue(product.isStatus());
        assertEquals(StatusProduct.DISPONIBLE, product.getStatusProduct());
        assertEquals(10, product.getStock());
    }
    @Test
    public void update_validNonStock(){
        product.setStock(0);
        productService.update(product);

        verify(productRepo).save(product);
        assertFalse(product.isStatus());
        assertEquals(StatusProduct.NODISPONIBLE, product.getStatusProduct());
        assertEquals(0, product.getStock());
    }

    //Prueba para activar un producto
    @Test
    public void active_valid(){
        productService.update(product);

        verify(productRepo).save(product);
        assertEquals(StatusProduct.NODISPONIBLE, product.getStatusProduct());
    }

    //Prueba para actualizar el stock después de una compra un producto
    @Test
    public void updateStockPurchase_valid(){
        productService.updateStockPurchase(product, 20000, 10);
        assertEquals(20000, product.getBuyPrice());
        assertEquals(10, product.getStock());
        verify(productRepo).save(product);
    }

    //Prueba para actualizar el stock después de una venta un producto
    @Test
    public void updateStockSale_valid(){
        product.setStock(30);
        productService.updateStockSale(product, 10);
        assertEquals(20, product.getStock());
        verify(productRepo).save(product);
    }

    //Prueba para eliminar un producto
    @Test
    public void delete_valid(){
        productService.delete(product);
        assertFalse(product.isStatus());
        assertEquals(StatusProduct.DESCONTINUADO, product.getStatusProduct());
        verify(productRepo).save(product);
    }

    //Prueba para obtener todos los productos
    @Test
    public void getAll_valid(){
        productService.getAll();
        verify(productRepo).findAll();
    }

    //Prueba para obtener producto por ID
    @Test
    public void getProductById_valid(){
        product.setId(1L);
        productService.getProductById(product.getId());
        verify(productRepo).findById(product.getId());
    }

    //Prueba para verificar las alertas creadas o cuáles se deben crear para cada situación
    @Test
    public void checkAndCreateAlertsByProduct_validNoStock(){
        product.setStock(0);
        product.setThreshold(3);
        product.setDescription("Product 1");
        List<Product> products = Collections.singletonList(product);
        String noStockDescription = "Sin Stock (" + product.getDescription() + " )";
        when(productRepo.findAll()).thenReturn(products);
        when(alertService.isAlertActive(product, noStockDescription)).thenReturn(false);
        productService.checkAndCreateAlertsByProduct();

        verify(productRepo, times(1)).findAll();
        verify(alertService, times(1)).isAlertActive(product, noStockDescription);
        verify(alertService, times(1)).createAlert(product, noStockDescription);
        verify(kardexService, times(1)).checkLowSales(product);
    }
    @Test
    public void checkAndCreateAlertsByProduct_validCriticalStock(){
        product.setStock(10);
        product.setThreshold(15);
        product.setDescription("Product 1");
        List<Product> products = Collections.singletonList(product);
        String criticalStockDescription = "Stock Crítico (" + product.getDescription() + " - " + product.getStock() + " unidades)";
        when(productRepo.findAll()).thenReturn(products);
        when(alertService.isAlertActive(product, criticalStockDescription)).thenReturn(false);
        productService.checkAndCreateAlertsByProduct();

        verify(productRepo, times(1)).findAll();
        verify(alertService, times(1)).isAlertActive(product, criticalStockDescription);
        verify(alertService, times(1)).createAlert(product, criticalStockDescription);
        verify(kardexService, times(1)).checkLowSales(product);
    }
    @Test
    public void checkAndCreateAlertsByProduct_validWithAlertActive(){
        product.setStock(0);
        product.setThreshold(15);
        product.setDescription("Product 1");
        List<Product> products = Collections.singletonList(product);
        String noStockDescription = "Sin Stock (" + product.getDescription() + " )";
        when(productRepo.findAll()).thenReturn(products);
        when(alertService.isAlertActive(product, noStockDescription)).thenReturn(true);
        productService.checkAndCreateAlertsByProduct();

        verify(productRepo, times(1)).findAll();
        verify(alertService, times(1)).isAlertActive(product, noStockDescription);
        verify(alertService, never()).createAlert(product, noStockDescription);
        verify(kardexService, times(1)).checkLowSales(product);
    }

    //Prueba para actualizar el stock y reserva de un producto después de un despacho
    @Test
    public void updateStockAndReserveDispatch_validSuccess(){
        product.setStock(30);
        product.setReserve(0);
        productService.updateStockAndReserveDispatch(product, 10, true);
        assertEquals(20, product.getStock());
        assertEquals(10, product.getReserve());
        verify(productRepo).save(product);
    }
    @Test
    public void updateStockAndReserveDispatch_validNoSuccess(){
        product.setStock(30);
        product.setReserve(10);
        productService.updateStockAndReserveDispatch(product, 10, false);
        assertEquals(40, product.getStock());
        assertEquals(0, product.getReserve());
        verify(productRepo).save(product);
    }
}
