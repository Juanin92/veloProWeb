package com.veloProWeb.service.Sale;

import com.veloProWeb.model.dto.DetailSaleDTO;
import com.veloProWeb.model.dto.DetailSaleRequestDTO;
import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.model.entity.customer.TicketHistory;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.model.entity.Sale.Dispatch;
import com.veloProWeb.model.entity.Sale.Sale;
import com.veloProWeb.model.entity.Sale.SaleDetail;
import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.repository.customer.TicketHistoryRepo;
import com.veloProWeb.repository.Sale.DispatchRepo;
import com.veloProWeb.repository.Sale.SaleDetailRepo;
import com.veloProWeb.service.customer.CustomerService;
import com.veloProWeb.service.product.ProductService;
import com.veloProWeb.service.inventory.KardexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleDetailServiceTest {
    @InjectMocks private SaleDetailService saleDetailService;
    @Mock private SaleDetailRepo saleDetailRepo;
    @Mock private TicketHistoryRepo ticketHistoryRepo;
    @Mock private DispatchRepo dispatchRepo;
    @Mock private SaleService saleService;
    @Mock private ProductService productService;
    @Mock private CustomerService customerService;
    @Mock private KardexService kardexService;
    @Mock private DispatchService dispatchService;
    @Mock private UserDetails userDetails;
    private DetailSaleDTO detailSaleDTO;
    private DetailSaleRequestDTO detailSaleRequestDTO;
    private SaleDetail saleDetail;
    private Sale sale;
    private Product product;
    private Customer customer;
    private TicketHistory ticketHistory;
    private Dispatch dispatch;

    @BeforeEach
    void setUp(){
        detailSaleDTO = new DetailSaleDTO();
        detailSaleDTO.setId(1L);
        detailSaleDTO.setIdProduct(2L);
        detailSaleDTO.setQuantity(1);

        detailSaleRequestDTO =  new DetailSaleRequestDTO();

        saleDetail = new SaleDetail();
        saleDetail.setId(3L);
        saleDetail.setQuantity(2);
        saleDetail.setPrice(100);

        sale = new Sale();
        sale.setId(1L);
        sale.setDocument("BO1");
        sale.setTotalSale(100);

        product =  new Product();
        product.setId(2L);
        product.setDescription("Prueba descripción");

        customer = new Customer();
        customer.setId(20L);
        customer.setName("Juan");
        customer.setSurname("Perez");

        ticketHistory = new TicketHistory();
        ticketHistory.setId(1L);
        ticketHistory.setStatus(true);
        ticketHistory.setNotificationsDate(null);
        ticketHistory.setDocument("BO1");

        dispatch = new Dispatch(1L,"#123", "En Preparación", "Calle 123",
                "TEST", "Cliente", false, LocalDate.now(), null,new ArrayList<>());
    }

    //Prueba para crear un detalle de venta
    @Test
    public void createSaleDetailsToSale_valid(){
        List<DetailSaleDTO> dtoList = Collections.singletonList(detailSaleDTO);
        when(productService.getProductById(detailSaleDTO.getIdProduct())).thenReturn(product);
        saleDetailService.createSaleDetailsToSale(dtoList, sale, userDetails);
        ArgumentCaptor<SaleDetail> saleDetailCaptor = ArgumentCaptor.forClass(SaleDetail.class);
        verify(saleDetailRepo).save(saleDetailCaptor.capture());
        SaleDetail savedSaleDetail = saleDetailCaptor.getValue();
        assertEquals(1, savedSaleDetail.getQuantity());
        assertEquals(product, savedSaleDetail.getProduct());
        verify(productService).updateStockSale(product, detailSaleDTO.getQuantity());
        verify(kardexService).addKardex(userDetails, savedSaleDetail.getProduct(), savedSaleDetail.getQuantity(),
                "Venta / " + sale.getDocument(), MovementsType.SALIDA);
    }
    @Test
    public void createSaleDetailsToSale_productNotFound(){
        List<DetailSaleDTO> dtoList = Collections.singletonList(detailSaleDTO);
        when(productService.getProductById(detailSaleDTO.getIdProduct())).thenThrow(new IllegalArgumentException("No ha seleccionado un producto registrado"));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> saleDetailService.createSaleDetailsToSale(dtoList, sale, userDetails));

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

    //Prueba para obtener una lista de dto con detalle de ventas
    @Test
    public void getSaleDetailsToSale_validWithCustomer(){
        saleDetail.setProduct(product);
        sale.setCustomer(customer);
        saleDetail.setSale(sale);
        ticketHistory.setCustomer(customer);
        List<SaleDetail> saleDetails = Collections.singletonList(saleDetail);
        List<TicketHistory> ticketHistoryList = Collections.singletonList(ticketHistory);
        when(saleDetailRepo.findBySaleId(1L)).thenReturn(saleDetails);
        when(saleService.getSaleById(1L)).thenReturn(Optional.of(sale));
        when(customerService.getCustomerById(20L)).thenReturn(customer);
        when(ticketHistoryRepo.findByCustomerId(20L)).thenReturn(ticketHistoryList);

        List<DetailSaleRequestDTO> result = saleDetailService.getSaleDetailsToSale(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        DetailSaleRequestDTO dto = result.getFirst();
        assertEquals(2, dto.getQuantity());
        assertEquals(100, dto.getPrice());
        assertEquals("Prueba descripción", dto.getDescriptionProduct());
        assertEquals("Juan Perez", dto.getCustomer());
        assertTrue(dto.isTicketStatus());
        assertNull(dto.getNotification());

        verify(saleDetailRepo).findBySaleId(1L);
        verify(saleService).getSaleById(1L);
        verify(customerService).getCustomerById(20L);
        verify(ticketHistoryRepo).findByCustomerId(20L);
    }
    @Test
    public void getSaleDetailsToSale_validWithoutCustomer(){
        saleDetail.setProduct(product);
        sale.setCustomer(null);
        saleDetail.setSale(sale);
        List<SaleDetail> saleDetails = Collections.singletonList(saleDetail);
        when(saleDetailRepo.findBySaleId(1L)).thenReturn(saleDetails);
        when(saleService.getSaleById(1L)).thenReturn(Optional.of(sale));

        List<DetailSaleRequestDTO> result = saleDetailService.getSaleDetailsToSale(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        DetailSaleRequestDTO dto = result.getFirst();
        assertEquals(2, dto.getQuantity());
        assertEquals(100, dto.getPrice());
        assertEquals("Prueba descripción", dto.getDescriptionProduct());
        assertEquals("", dto.getCustomer());
        assertTrue(dto.isTicketStatus());
        assertNull(dto.getNotification());

        verify(saleDetailRepo).findBySaleId(1L);
        verify(saleService).getSaleById(1L);
        verify(customerService, never()).getCustomerById(20L);
        verify(ticketHistoryRepo, never()).findByCustomerId(20L);
    }

    //Prueba para crear detalle de venta para un despacho
    @Test
    public void createSaleDetailsToDispatch_valid(){
        List<DetailSaleDTO> detailSaleDTOS = Collections.singletonList(detailSaleDTO);
        when(productService.getProductById(2L)).thenReturn(product);
        saleDetailService.createSaleDetailsToDispatch(detailSaleDTOS, dispatch);

        ArgumentCaptor<SaleDetail> detailArgumentCaptor = ArgumentCaptor.forClass(SaleDetail.class);
        verify(saleDetailRepo, times(1)).save(detailArgumentCaptor.capture());
        SaleDetail saleDetailSaved = detailArgumentCaptor.getValue();
        assertEquals(product, saleDetailSaved.getProduct());
        assertEquals(dispatch, saleDetailSaved.getDispatch());
    }

    //Prueba para obtener una lista de dto con detalle de ventas de un despacho
    @Test
    public void getSaleDetailsToDispatch_valid(){
        saleDetail.setProduct(product);
        saleDetail.setDispatch(dispatch);
        List<SaleDetail> saleDetails = Collections.singletonList(saleDetail);
        when(saleDetailRepo.findByDispatchId(1L)).thenReturn(saleDetails);
        when(dispatchService.getDispatchById(dispatch.getId())).thenReturn(Optional.of(dispatch));

        List<DetailSaleRequestDTO> result = saleDetailService.getSaleDetailsToDispatch(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        DetailSaleRequestDTO dto = result.getFirst();
        assertEquals(2, dto.getQuantity());
        assertEquals(100, dto.getPrice());
        assertEquals("Prueba descripción", dto.getDescriptionProduct());
        assertFalse(dto.isTicketStatus());
        assertNull(dto.getNotification());
        assertNull(dto.getCustomer());

        verify(saleDetailRepo, times(1)).findByDispatchId(1L);
        verify(dispatchService, times(1)).getDispatchById(1L);
    }

    //Prueba para agregar un venta al detalle de venta de un despacho
    @Test
    public void addSaleToSaleDetailsDispatch_valid(){
        saleDetail.setProduct(product);
        saleDetail.setDispatch(dispatch);
        List<SaleDetail> saleDetails = Collections.singletonList(saleDetail);
        when(saleDetailRepo.findByDispatchId(1L)).thenReturn(saleDetails);
        when(dispatchService.getDispatchById(dispatch.getId())).thenReturn(Optional.of(dispatch));

        saleDetailService.addSaleToSaleDetailsDispatch(1L, sale);
        verify(dispatchService, times(1)).getDispatchById(1L);
        verify(saleDetailRepo, times(1)).findByDispatchId(1L);
        verify(productService, times(1)).updateStockAndReserveDispatch(saleDetail.getProduct(), saleDetail.getQuantity(), false);
        verify(dispatchRepo, times(1)).save(dispatch);

        assertEquals(sale, saleDetail.getSale());
        assertTrue(dispatch.isHasSale());
    }
}
