package com.veloProWeb.Service.Sale;

import com.veloProWeb.Model.DTO.DetailSaleDTO;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Entity.Sale.SaleDetail;
import com.veloProWeb.Repository.Sale.SaleDetailRepo;
import com.veloProWeb.Service.Product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleDetailServiceTest {
    @InjectMocks private SaleDetailService saleDetailService;
    @Mock private SaleDetailRepo saleDetailRepo;
    @Mock private ProductService productService;
    private DetailSaleDTO detailSaleDTO;
    private Sale sale;
    private Product product;

    @BeforeEach
    void setUp(){
        detailSaleDTO = new DetailSaleDTO();
        detailSaleDTO.setId(1L);
        detailSaleDTO.setIdProduct(2L);
        detailSaleDTO.setQuantity(1);

        sale = new Sale();
        sale.setId(1L);

        product =  new Product();
        product.setId(2L);
    }

    //Prueba para crear un detalle de venta
    @Test
    public void createSaleDetails_valid(){
        List<DetailSaleDTO> dtoList = Collections.singletonList(detailSaleDTO);
        when(productService.getProductById(detailSaleDTO.getIdProduct())).thenReturn(product);
        saleDetailService.createSaleDetails(dtoList, sale);
        ArgumentCaptor<SaleDetail> saleDetailCaptor = ArgumentCaptor.forClass(SaleDetail.class);
        verify(saleDetailRepo).save(saleDetailCaptor.capture());
        SaleDetail savedSaleDetail = saleDetailCaptor.getValue();
        assertEquals(1, savedSaleDetail.getQuantity());
        assertEquals(product, savedSaleDetail.getProduct());
        verify(productService).updateStockSale(product, detailSaleDTO.getQuantity());
    }
    @Test
    public void createSaleDetails_productNotFound(){
        List<DetailSaleDTO> dtoList = Collections.singletonList(detailSaleDTO);
        when(productService.getProductById(detailSaleDTO.getIdProduct())).thenThrow(new IllegalArgumentException("No ha seleccionado un producto registrado"));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> saleDetailService.createSaleDetails(dtoList, sale));

        assertEquals( "No ha seleccionado un producto registrado", exception.getMessage());
        verify(saleDetailRepo, never()).save(any(SaleDetail.class));
        verify(productService).getProductById(detailSaleDTO.getIdProduct());
    }

    //Prueba para obtener todos los detalles de ventas
    @Test
    public void getAll_valid(){
        SaleDetail saleDetail = new SaleDetail();
        List<SaleDetail> saleDetailList = Collections.singletonList(saleDetail);
        when(saleDetailService.getAll()).thenReturn(saleDetailList);
        List<SaleDetail> result = saleDetailService.getAll();
        verify(saleDetailRepo).findAll();
        assertEquals(1, result.size());
    }
}
