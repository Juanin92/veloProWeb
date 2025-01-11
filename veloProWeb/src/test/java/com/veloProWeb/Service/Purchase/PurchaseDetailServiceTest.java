package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.DTO.DetailPurchaseDTO;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.PurchaseDetail;
import com.veloProWeb.Repository.Purchase.PurchaseDetailRepo;
import com.veloProWeb.Service.Product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PurchaseDetailServiceTest {

    @InjectMocks private PurchaseDetailService purchaseDetailService;
    @Mock private PurchaseDetailRepo purchaseDetailRepo;
    @Mock private ProductService productService;
    private Purchase purchase;
    private Product product;
    private DetailPurchaseDTO dto;

    @BeforeEach
    void setUp(){
        dto = new DetailPurchaseDTO();
        dto.setId(1L);
        dto.setIdProduct(5L);
        dto.setIdPurchase(2L);
        dto.setPrice(20000);
        dto.setTax(2000);
        dto.setQuantity(1);
        dto.setTotal(22000);

        purchase = new Purchase();
        purchase.setId(2L);

        product = new Product();
        product.setId(5L);
    }

    //Prueba para crear un detalle de compra
    @Test
    public void createDetailPurchase_valid(){
        List<DetailPurchaseDTO> dtoList = Collections.singletonList(dto);
        when(productService.getProductById(product.getId())).thenReturn(product);
        purchaseDetailService.createDetailPurchase(dtoList, purchase);
        ArgumentCaptor<PurchaseDetail> purchaseDetailCaptor = ArgumentCaptor.forClass(PurchaseDetail.class);
        verify(purchaseDetailRepo).save(purchaseDetailCaptor.capture());
        PurchaseDetail savedPurchaseDetail = purchaseDetailCaptor.getValue();
        assertEquals(20000, savedPurchaseDetail.getPrice());
        assertEquals(1, savedPurchaseDetail.getQuantity());
        assertEquals(2000, savedPurchaseDetail.getTax());
        assertEquals(22000, savedPurchaseDetail.getTotal());
        assertEquals(2L, savedPurchaseDetail.getPurchase().getId());
        assertEquals(5L, savedPurchaseDetail.getProduct().getId());
    }

    //Prueba para obtener todos los detalles de compras
    @Test
    public void getAll_valid(){
        PurchaseDetail purchaseDetail = new PurchaseDetail();
        PurchaseDetail purchaseDetail2 = new PurchaseDetail();
        List<PurchaseDetail> purchaseDetailList = Arrays.asList(purchaseDetail, purchaseDetail2);
        when(purchaseDetailService.getAll()).thenReturn(purchaseDetailList);
        List<PurchaseDetail> result = purchaseDetailService.getAll();
        verify(purchaseDetailRepo).findAll();
        assertEquals(2, result.size());
    }
}
