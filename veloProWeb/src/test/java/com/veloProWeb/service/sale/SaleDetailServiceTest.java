package com.veloProWeb.service.sale;

import com.veloProWeb.exceptions.sale.SaleNotFoundException;
import com.veloProWeb.mapper.SaleMapper;
import com.veloProWeb.model.Enum.PaymentMethod;
import com.veloProWeb.model.dto.sale.SaleDetailRequestDTO;
import com.veloProWeb.model.entity.Sale.Dispatch;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.model.entity.Sale.Sale;
import com.veloProWeb.model.entity.Sale.SaleDetail;
import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.repository.Sale.DispatchRepo;
import com.veloProWeb.repository.Sale.SaleDetailRepo;
import com.veloProWeb.service.product.ProductService;
import com.veloProWeb.service.inventory.KardexService;
import com.veloProWeb.validation.SaleValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleDetailServiceTest {
    @InjectMocks private SaleDetailService saleDetailService;
    @Mock private SaleDetailRepo saleDetailRepo;
    @Mock private DispatchRepo dispatchRepo;
    @Mock private ProductService productService;
    @Mock private KardexService kardexService;
    @Mock private DispatchService dispatchService;
    @Mock private SaleValidator validator;
    @Mock private UserDetails userDetails;
    @Spy private SaleMapper mapper;

    //Prueba para crear un detalle de venta
    @Test
    public void addDetailsToSale(){
        Product product = Product.builder().id(1L).description("Product Description").salePrice(100).build();
        Sale sale = Sale.builder().id(1L).paymentMethod(PaymentMethod.EFECTIVO).totalSale(1000).tax(10)
                .discount(0).document("0001").build();
        SaleDetailRequestDTO requestDTO = SaleDetailRequestDTO.builder().idProduct(1L).quantity(10).build();

        doNothing().when(validator).hasSale(sale);
        when(productService.getProductById(requestDTO.getIdProduct())).thenReturn(product);
        doNothing().when(productService).updateStockSale(product, requestDTO.getQuantity());
        doNothing().when(kardexService).addKardex(userDetails, product, requestDTO.getQuantity(), "Venta "
                        + sale.getDocument(), MovementsType.SALIDA);

        SaleDetail detailMapped = mapper.toSaleDetailEntityFromSale(requestDTO, product, sale);
        ArgumentCaptor<SaleDetail> saleDetailCaptor = ArgumentCaptor.forClass(SaleDetail.class);
        saleDetailService.addDetailsToSale(List.of(requestDTO), sale, userDetails);

        verify(validator, times(1)).hasSale(sale);
        verify(productService, times(1)).updateStockSale(product, requestDTO.getQuantity());
        verify(kardexService, times(1)).addKardex(userDetails, product, requestDTO.getQuantity(),
                "Venta " + sale.getDocument(), MovementsType.SALIDA);
        verify(saleDetailRepo, times(1)).save(saleDetailCaptor.capture());

        assertEquals(product, detailMapped.getProduct());
        assertEquals(10, detailMapped.getQuantity());
        assertEquals(119, detailMapped.getPrice());
        assertEquals(19, detailMapped.getTax());
        assertEquals(1190, detailMapped.getTotal());
        assertEquals(sale, detailMapped.getSale());
        assertNull(detailMapped.getDispatch());
    }
    @Test
    public void add_saleNotFound(){
        doThrow(new SaleNotFoundException("Venta no encontrada")).when(validator).hasSale(null);
        SaleNotFoundException exception = assertThrows(SaleNotFoundException.class,
                () -> saleDetailService.addDetailsToSale(List.of(), any(Sale.class), userDetails));

        verify(saleDetailRepo, never()).save(any(SaleDetail.class));
        assertEquals( "Venta no encontrada", exception.getMessage());
    }

    //Prueba para crear detalle de venta para un despacho
    @Test
    void createSaleDetailsToDispatch() {
        SaleDetailRequestDTO dto = SaleDetailRequestDTO.builder().idProduct(1L).quantity(1).build();
        Dispatch dispatch = Dispatch.builder().id(1L).build();
        Product product = Product.builder().id(1L).description("Product test").salePrice(100).build();
        when(productService.getProductById(dto.getIdProduct())).thenReturn(product);
        SaleDetail saleDetailMapped = mapper.toSaleDetailEntityFromDispatch(dto, product, dispatch);

        saleDetailService.createSaleDetailsToDispatch(List.of(dto), dispatch);

        verify(productService, times(1)).getProductById(dto.getIdProduct());
        verify(mapper, times(2)).toSaleDetailEntityFromDispatch(dto, product, dispatch);
        verify(saleDetailRepo, times(1)).save(saleDetailMapped);
        verify(productService, times(1)).updateStockAndReserveDispatch(product,
                saleDetailMapped.getQuantity(), true);

        assertEquals(dispatch, saleDetailMapped.getDispatch());
        assertEquals(product, saleDetailMapped.getProduct());
        assertEquals(1, saleDetailMapped.getQuantity());
        assertEquals(119, saleDetailMapped.getPrice());
        assertNull(saleDetailMapped.getSale());
    }

    //Prueba para agregar un venta al detalle de venta de un despacho
    @Test
    void addSaleToSaleDetailsDispatch() {
        Sale sale = Sale.builder().build();
        Dispatch dispatch = Dispatch.builder().id(1L).build();
        when(dispatchService.getDispatchById(1L)).thenReturn(dispatch);

        Product product = Product.builder().id(1L).description("Product test").salePrice(100).build();
        SaleDetail saleDetail = SaleDetail.builder().dispatch(dispatch).quantity(1).product(product).build();
        when(saleDetailRepo.findByDispatchId(dispatch.getId())).thenReturn(List.of(saleDetail));

        saleDetailService.addSaleToSaleDetailsDispatch(1L, sale);

        ArgumentCaptor<SaleDetail> saleDetailCaptor = ArgumentCaptor.forClass(SaleDetail.class);
        ArgumentCaptor<Dispatch> dispatchCaptor = ArgumentCaptor.forClass(Dispatch.class);
        verify(dispatchService, times(1)).getDispatchById(1L);
        verify(saleDetailRepo, times(1)).findByDispatchId(dispatch.getId());
        verify(saleDetailRepo, times(1)).save(saleDetailCaptor.capture());
        verify(productService, times(1)).updateStockAndReserveDispatch(saleDetail.getProduct(),
                saleDetail.getQuantity(), false);
        verify(productService, times(1)).updateStockSale(saleDetail.getProduct(),
                saleDetail.getQuantity());
        verify(dispatchRepo, times(1)).save(dispatchCaptor.capture());

        SaleDetail saleDetailResult = saleDetailCaptor.getValue();
        Dispatch dispatchResult = dispatchCaptor.getValue();
        assertEquals(sale, saleDetailResult.getSale());
        assertTrue(dispatchResult.isHasSale());
    }
}
