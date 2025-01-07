package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Repository.Purchase.PurchaseRepo;
import com.veloProWeb.Validation.PurchaseValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {

    @InjectMocks private PurchaseService purchaseService;
    @Mock private PurchaseRepo purchaseRepo;
    @Mock private PurchaseValidator validator;
    @Mock private Supplier supplier;
    private Purchase purchase;

    @BeforeEach
    void setUp(){
        purchase = new Purchase();
        purchase.setId(1L);
        purchase.setDocument("A001");
        purchase.setDocumentType("Boleta");
        purchase.setIva(1500);
        purchase.setPurchaseTotal(20000);
        purchase.setDate(LocalDate.now());
        purchase.setSupplier(supplier);
        purchase.setPurchaseDetails(new ArrayList<>());
    }

    //Prueba para crear una nueva compra
    @Test
    public void save_valid(){
        purchase.setId(null);
        purchaseService.save(LocalDate.now(),supplier,"Boleta","A001",1500, 20000);
        verify(validator).validate(purchase);
        verify(purchaseRepo).save(purchase);
    }

    //Prueba para obtener el total de compras realizadas
    @Test
    public void totalPurchase_valid(){
        when(purchaseRepo.count()).thenReturn(1L);
        Long totalPurchase =  purchaseService.totalPurchase();
        verify(purchaseRepo).count();
        assertEquals(1L, totalPurchase);
    }
}
