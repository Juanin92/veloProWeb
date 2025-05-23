package com.veloProWeb.service.purchase;

import com.veloProWeb.mapper.PurchaseMapper;
import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.model.dto.purchase.PurchaseDetailRequestDTO;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.model.entity.purchase.Purchase;
import com.veloProWeb.model.entity.purchase.PurchaseDetail;
import com.veloProWeb.repository.purchase.PurchaseDetailRepo;
import com.veloProWeb.service.Report.KardexService;
import com.veloProWeb.service.product.ProductService;
import com.veloProWeb.validation.PurchaseValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseDetailServiceTest {

    @InjectMocks private PurchaseDetailService purchaseDetailService;
    @Mock private PurchaseDetailRepo purchaseDetailRepo;
    @Mock private ProductService productService;
    @Mock private KardexService kardexService;
    @Mock private PurchaseMapper mapper;
    @Mock private PurchaseValidator validator;
    @Mock private UserDetails userDetails;

    //Prueba para crear un detalle de compra
    @Test
    public void createPurchase_Detail_valid(){
        Purchase purchase = Purchase.builder().id(1L).documentType("Factura").document("F-100").build();
        doNothing().when(validator).hasPurchase(purchase);
        PurchaseDetailRequestDTO detail = PurchaseDetailRequestDTO.builder().idPurchase(1L).idProduct(1L)
                .tax(100).total(1000).quantity(1).build();
        List<PurchaseDetailRequestDTO> dtoList = List.of(detail);
        Product product = Product.builder().id(1L).build();
        when(productService.getProductById(1L)).thenReturn(product);
        PurchaseDetail purchaseDetail = PurchaseDetail.builder().price(1000).quantity(1).purchase(purchase)
                .product(product).build();
        when(mapper.toPurchaseDetailEntity(detail, product, purchase)).thenReturn(purchaseDetail);
        doNothing().when(productService).updateStockPurchase(product, purchaseDetail.getPrice(),
                purchaseDetail.getQuantity());
        doNothing().when(kardexService).addKardex(userDetails, product, 1, String.format("Compra / %s - %s",
                purchase.getDocumentType(), purchase.getDocument()), MovementsType.ENTRADA);

        ArgumentCaptor<PurchaseDetail> purchaseDetailCaptor = ArgumentCaptor.forClass(PurchaseDetail.class);
        purchaseDetailService.createPurchaseDetail(userDetails, dtoList, purchase);

        verify(purchaseDetailRepo, times(1)).save(purchaseDetailCaptor.capture());
        verify(validator, times(1)).hasPurchase(purchase);
        verify(mapper, times(1)).toPurchaseDetailEntity(detail, product, purchase);
        verify(productService, times(1)).getProductById(1L);
        verify(productService, times(1)).updateStockPurchase(product, 1000, 1);
        verify(kardexService, times(1)).addKardex(userDetails, product, 1,
                String.format("Compra / %s - %s", "Factura", "F-100"), MovementsType.ENTRADA);

        PurchaseDetail result = purchaseDetailCaptor.getValue();
        assertEquals( 1000, result.getPrice());
        assertEquals( 1, result.getQuantity());
        assertEquals(product, result.getProduct());
        assertEquals(purchase, result.getPurchase());
    }
}
