package com.veloProWeb.service.Purchase;

import com.veloProWeb.model.dto.DetailPurchaseDTO;
import com.veloProWeb.model.dto.DetailPurchaseRequestDTO;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.model.entity.Purchase.Purchase;
import com.veloProWeb.model.entity.Purchase.PurchaseDetail;
import com.veloProWeb.repository.Purchase.PurchaseDetailRepo;
import com.veloProWeb.service.product.ProductService;
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
    @Mock private PurchaseService purchaseService;
    private Purchase purchase;
    private Product product;
    private DetailPurchaseDTO dto;
    private DetailPurchaseRequestDTO detailDto;
    private PurchaseDetail purchaseDetail;

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
        product.setDescription("prueba");

        purchaseDetail = new PurchaseDetail();
        purchaseDetail.setPurchase(purchase);
        purchaseDetail.setProduct(product);
        purchaseDetail.setTax(100);
        purchaseDetail.setPrice(10000);
        purchaseDetail.setQuantity(2);
        purchaseDetail.setTotal(20200);

        detailDto =  new DetailPurchaseRequestDTO();
        detailDto.setDescriptionProduct(product.getDescription());
        detailDto.setPrice(purchaseDetail.getPrice());
        detailDto.setQuantity(purchaseDetail.getQuantity());
        detailDto.setTotal(purchaseDetail.getTotal());
        detailDto.setTax(purchaseDetail.getTax());
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
    public void getPurchaseDetails_valid(){
        List<PurchaseDetail> purchaseDetails = Collections.singletonList(purchaseDetail);
        when(purchaseDetailRepo.findByPurchaseId(purchase.getId())).thenReturn(purchaseDetails);
        when(purchaseService.getPurchaseById(purchase.getId())).thenReturn(Optional.of(purchase));
        List<DetailPurchaseRequestDTO> result = purchaseDetailService.getPurchaseDetails(purchase.getId());
        verify(purchaseDetailRepo).findByPurchaseId(purchase.getId());
        verify(purchaseService).getPurchaseById(purchase.getId());

        assertEquals(1, result.size());
        DetailPurchaseRequestDTO resultDto = result.get(0);
        assertEquals(product.getDescription(), resultDto.getDescriptionProduct());
        assertEquals(purchaseDetail.getPrice(), resultDto.getPrice());
        assertEquals(purchaseDetail.getQuantity(), resultDto.getQuantity());
        assertEquals(purchaseDetail.getTax(), resultDto.getTax());
        assertEquals(purchaseDetail.getTotal(), resultDto.getTotal());
    }
}
