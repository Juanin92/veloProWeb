package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.DTO.PurchaseRequestDTO;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.Supplier;
import com.veloProWeb.Repository.Purchase.PurchaseRepo;
import com.veloProWeb.Repository.Purchase.SupplierRepo;
import com.veloProWeb.Validation.PurchaseValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {

    @InjectMocks private PurchaseService purchaseService;
    @Mock private PurchaseRepo purchaseRepo;
    @Mock private SupplierRepo supplierRepo;
    @Mock private PurchaseValidator validator;
    private PurchaseRequestDTO purchaseDTO;
    private Supplier supplier;

    @BeforeEach
    void setUp(){
        purchaseDTO = new PurchaseRequestDTO();
        purchaseDTO.setId(1L);
        purchaseDTO.setDate(LocalDate.now());
        purchaseDTO.setIdSupplier(2L);
        purchaseDTO.setDocument("A001");
        purchaseDTO.setDocumentType("Boleta");
        purchaseDTO.setTax(1500);
        purchaseDTO.setTotal(20000);
        purchaseDTO.setDetailList(new ArrayList<>());

        supplier =  new Supplier();
        supplier.setId(3L);
    }

    //Prueba para crear una nueva compra
    @Test
    public void createPurchase_valid() {
        when(supplierRepo.findById(purchaseDTO.getIdSupplier())).thenReturn(Optional.of(supplier));
        Purchase createdPurchase = purchaseService.createPurchase(purchaseDTO);
        ArgumentCaptor<Purchase> purchaseCaptor = ArgumentCaptor.forClass(Purchase.class);
        verify(purchaseRepo).save(purchaseCaptor.capture());
        Purchase savedPurchase = purchaseCaptor.getValue();
        assertEquals("A001", savedPurchase.getDocument());
        assertEquals(3L, savedPurchase.getSupplier().getId());
        verify(supplierRepo).findById(purchaseDTO.getIdSupplier());
        verify(validator).validate(savedPurchase);
    }
    @Test
    public void createPurchase_supplierNotFound() {
        when(supplierRepo.findById(purchaseDTO.getIdSupplier())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> purchaseService.createPurchase(purchaseDTO));

        assertEquals("No ha seleccionado un proveedor", exception.getMessage());
        verify(supplierRepo).findById(purchaseDTO.getIdSupplier());
        verifyNoInteractions(purchaseRepo);
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
