package com.veloproweb.service.purchase;

import com.veloproweb.exceptions.supplier.SupplierNotFoundException;
import com.veloproweb.mapper.PurchaseMapper;
import com.veloproweb.model.dto.purchase.PurchaseDetailRequestDTO;
import com.veloproweb.model.dto.purchase.PurchaseRequestDTO;
import com.veloproweb.model.dto.purchase.PurchaseResponseDTO;
import com.veloproweb.model.entity.purchase.Purchase;
import com.veloproweb.model.entity.purchase.Supplier;
import com.veloproweb.repository.purchase.PurchaseRepo;
import com.veloproweb.service.purchase.interfaces.ISupplierService;
import com.veloproweb.validation.PurchaseValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseServiceTest {

    @InjectMocks private PurchaseService purchaseService;
    @Mock private PurchaseRepo purchaseRepo;
    @Mock private ISupplierService supplierService;
    @Mock private PurchaseMapper mapper;
    @Mock private PurchaseValidator validator;

    @BeforeEach
    void setUp(){
    }

    //Prueba para crear una nueva compra
    @Test
    void createPurchase_valid() {
        Supplier supplier = Supplier.builder().id(1L).rut("12345678-9").build();
        PurchaseDetailRequestDTO detailDto = PurchaseDetailRequestDTO.builder().build();
        PurchaseRequestDTO dto = PurchaseRequestDTO.builder().supplier("12345678-9").total(1000).tax(100)
                .document("F-100").documentType("Factura").detailList(List.of(detailDto)).build();
        when(supplierService.getEntityByRut(dto.getSupplier())).thenReturn(supplier);
        Purchase purchaseMapped = Purchase.builder().build();
        when(mapper.toPurchaseEntity(dto, supplier)).thenReturn(purchaseMapped);
        doNothing().when(validator).validateTotal(dto.getTotal(), dto.getDetailList());

        Purchase createdPurchase = purchaseService.createPurchase(dto);
        ArgumentCaptor<Purchase> purchaseCaptor = ArgumentCaptor.forClass(Purchase.class);

        verify(purchaseRepo, times(1)).save(purchaseCaptor.capture());
        verify(supplierService, times(1)).getEntityByRut(dto.getSupplier());
        verify(mapper, times(1)).toPurchaseEntity(dto, supplier);

        Purchase result = purchaseCaptor.getValue();
        assertEquals(result.getSupplier(), createdPurchase.getSupplier());
        assertEquals(result.getDocumentType(), createdPurchase.getDocumentType());
        assertEquals(result.getDocument(), createdPurchase.getDocument());
        assertEquals(result.getPurchaseTotal(), createdPurchase.getPurchaseTotal());
    }
    @Test
    void createPurchase_supplierNotFound() {
        PurchaseRequestDTO dto = PurchaseRequestDTO.builder().supplier("12345678-9").total(1000).tax(100)
                .document("F-100").documentType("Factura").build();
        doThrow(new SupplierNotFoundException("Proveedor no encontrado")).when(supplierService)
                .getEntityByRut(dto.getSupplier());
        SupplierNotFoundException e = assertThrows(SupplierNotFoundException.class,
                () -> purchaseService.createPurchase(dto));

        verify(supplierService, times(1)).getEntityByRut(dto.getSupplier());
        verifyNoInteractions(purchaseRepo, mapper);

        assertEquals("Proveedor no encontrado", e.getMessage());
    }

    //Prueba para obtener el total de compras realizadas
    @Test
    void totalPurchase_valid(){
        when(purchaseRepo.count()).thenReturn(1L);
        Long totalPurchase =  purchaseService.totalPurchase();
        verify(purchaseRepo).count();
        assertEquals(1L, totalPurchase);
    }

    //Prueba para obtener todas las compras
    @Test
    void getAllPurchases_valid(){
        Supplier supplier = Supplier.builder().id(1L).rut("12345678-9").name("Sony").build();
        Purchase purchase = Purchase.builder().supplier(supplier).purchaseTotal(1000).iva(100)
                .document("F-100").documentType("Factura").build();
        Purchase purchase1 = Purchase.builder().supplier(supplier).purchaseTotal(10000).iva(1000)
                .document("F-150").documentType("Factura").build();
        Purchase purchase2 = Purchase.builder().supplier(supplier).purchaseTotal(2000).iva(200)
                .document("B-10").documentType("Boleta").build();
        when(purchaseRepo.findAll()).thenReturn(List.of(purchase, purchase1, purchase2));
        PurchaseResponseDTO dtoMapped = PurchaseResponseDTO.builder().supplier(supplier.getName()).purchaseTotal(1000)
                .iva(100).documentType("Factura").document("F-100").build();
        when(mapper.toPurchaseResponseDTO(purchase)).thenReturn(dtoMapped);

        List<PurchaseResponseDTO> result = purchaseService.getAllPurchases();

        verify(purchaseRepo, times(1)).findAll();
        verify(mapper, times(1)).toPurchaseResponseDTO(purchase);

        assertEquals(result.getFirst().getSupplier(), purchase.getSupplier().getName());
        assertEquals(1000, result.getFirst().getPurchaseTotal());
        assertEquals("Factura", result.getFirst().getDocumentType());
    }
}
