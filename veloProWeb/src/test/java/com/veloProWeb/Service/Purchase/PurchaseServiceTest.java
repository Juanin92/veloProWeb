package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.DTO.DetailPurchaseDTO;
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
import java.util.Arrays;
import java.util.List;

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
        purchaseService.createPurchase(LocalDate.now(),supplier,"Boleta","A001",1500, 20000);
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

    //Prueba para obtener el total monetario de una lista de DTO
    @Test
    public void totalPricePurchase_valid(){
        DetailPurchaseDTO dto = new DetailPurchaseDTO();
        dto.setIdProduct(1L);
        dto.setBrand("Samsung");
        dto.setDescription("phone");
        dto.setPrice(25000);
        dto.setTax(2500);
        dto.setQuantity(1);
        dto.setTotal(27500);

        DetailPurchaseDTO dto2 = new DetailPurchaseDTO();
        dto2.setIdProduct(1L);
        dto2.setBrand("Samsung");
        dto2.setDescription("tv");
        dto2.setPrice(250000);
        dto2.setTax(25000);
        dto2.setQuantity(1);
        dto2.setTotal(275000);
        List<DetailPurchaseDTO> dtoList = Arrays.asList(dto,dto2);
        int total = purchaseService.totalPricePurchase(dtoList);
        assertEquals(302500, total);
    }
}
