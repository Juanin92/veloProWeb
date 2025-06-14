package com.veloproweb.scheduler;

import com.veloproweb.model.entity.product.Product;
import com.veloproweb.repository.product.ProductRepo;
import com.veloproweb.service.product.interfaces.IProductEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ProductScheduledTasksTest {

    @InjectMocks private ProductScheduledTasks productScheduledTasks;
    @Mock private ProductRepo productRepo;
    @Mock private IProductEventService productEventService;
    private Product product2, product3, product4, product5;

    @BeforeEach
    void setUp(){
        product2 = Product.builder().id(2L).description("Product 2").threshold(5).stock(2).build();
        product3 = Product.builder().id(3L).description("Product 3").threshold(5).stock(0).build();
        product4 = Product.builder().id(4L).description("Product 4").threshold(5).stock(1).build();
        product5 = Product.builder().id(5L).description("Product 5").threshold(5).stock(0).build();
    }

    //Prueba para verificar las alertas creadas o cuáles se deben crear para cada situación
    @Test
    public void checkAndCreateAlertsByProduct_valid(){
        when(productRepo.findOutOfStock()).thenReturn(List.of(product3, product5));
        when(productRepo.findCriticalStock()).thenReturn(List.of(product2 ,product4));

        productScheduledTasks.checkAndHandleProductAlerts();

        verify(productRepo, times(1)).findOutOfStock();
        verify(productRepo, times(1)).findCriticalStock();
        verify(productEventService, times(1)).checkLowSales(product3);
        verify(productEventService, times(1)).checkLowSales(product4);
    }
}
