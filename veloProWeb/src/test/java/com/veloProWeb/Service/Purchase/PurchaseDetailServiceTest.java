package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.DTO.DetailPurchaseDTO;
import com.veloProWeb.Model.Entity.Product.BrandProduct;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.PurchaseDetail;
import com.veloProWeb.Repository.Purchase.PurchaseDetailRepo;
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
    private Purchase purchase;
    private Product product;
    private DetailPurchaseDTO dto;

    @BeforeEach
    void setUp(){
        dto = new DetailPurchaseDTO();
        dto.setIdProduct(1L);
        dto.setBrand("Samsung");
        dto.setDescription("phone");
        dto.setPrice(20000);
        dto.setTax(2000);
        dto.setQuantity(1);
        dto.setTotal(22000);

        purchase = new Purchase();
        purchase.setId(1L);

        product = new Product();
        product.setId(1L);
    }

    //Prueba para crear un detalle de compra
    @Test
    public void createDetailPurchase_valid(){
        purchaseDetailService.createDetailPurchase(dto, purchase, product);
        ArgumentCaptor<PurchaseDetail> purchaseDetailCaptor = ArgumentCaptor.forClass(PurchaseDetail.class);
        verify(purchaseDetailRepo).save(purchaseDetailCaptor.capture());
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

    //Prueba para crear un dto de detalle de compras
    @Test
    public void createDTO_valid(){
        BrandProduct brand = new BrandProduct();
        brand.setName("Samsung");
        product.setBrand(brand);
        DetailPurchaseDTO dto = purchaseDetailService.createDTO(product);
        assertNotNull(dto);
        assertEquals(1L, dto.getIdProduct());
    }
    @Test
    public void createDTO_validNull(){
        DetailPurchaseDTO dto = purchaseDetailService.createDTO(null);
        assertNull(dto);
    }

    //Prueba para eliminar un producto del detalle de compra
    @Test
    public void deleteProduct_valid(){
        DetailPurchaseDTO dto = new DetailPurchaseDTO();
        dto.setIdProduct(1L);
        List<DetailPurchaseDTO> dtoList = new ArrayList<>();
        dtoList.add(dto);

        boolean result = purchaseDetailService.deleteProduct(1L, dtoList);
         assertTrue(result);
         assertEquals(0, dtoList.size());
    }
    @Test
    public void deleteProduct_validNotFound(){
        DetailPurchaseDTO dto = new DetailPurchaseDTO();
        dto.setIdProduct(1L);
        List<DetailPurchaseDTO> dtoList = new ArrayList<>();
        dtoList.add(dto);

        boolean result = purchaseDetailService.deleteProduct(2L, dtoList);
        assertFalse(result);
        assertEquals(1, dtoList.size());
    }
}
