package com.veloProWeb.Service.Sale;

import com.veloProWeb.Model.DTO.DetailSaleDTO;
import com.veloProWeb.Model.DTO.DetailSaleRequestDTO;
import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Entity.Sale.SaleDetail;
import com.veloProWeb.Repository.Customer.TicketHistoryRepo;
import com.veloProWeb.Repository.Sale.SaleDetailRepo;
import com.veloProWeb.Service.Customer.CustomerService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleDetailServiceTest {
    @InjectMocks private SaleDetailService saleDetailService;
    @Mock private SaleDetailRepo saleDetailRepo;
    @Mock private TicketHistoryRepo ticketHistoryRepo;
    @Mock private SaleService saleService;
    @Mock private ProductService productService;
    @Mock private CustomerService customerService;
    private DetailSaleDTO detailSaleDTO;
    private DetailSaleRequestDTO detailSaleRequestDTO;
    private SaleDetail saleDetail;
    private Sale sale;
    private Product product;
    private Customer customer;
    private TicketHistory ticketHistory;

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

    //Prueba para obtener una lista de DTO con detalle de ventas
    @Test
    public void getSaleDetails_validWithCustomer(){
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

        List<DetailSaleRequestDTO> result = saleDetailService.getSaleDetails(1L);
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
    public void getSaleDetails_validWithoutCustomer(){
        saleDetail.setProduct(product);
        sale.setCustomer(null);
        saleDetail.setSale(sale);
        List<SaleDetail> saleDetails = Collections.singletonList(saleDetail);
        when(saleDetailRepo.findBySaleId(1L)).thenReturn(saleDetails);
        when(saleService.getSaleById(1L)).thenReturn(Optional.of(sale));

        List<DetailSaleRequestDTO> result = saleDetailService.getSaleDetails(1L);
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
}
