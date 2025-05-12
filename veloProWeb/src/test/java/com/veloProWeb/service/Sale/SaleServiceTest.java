package com.veloProWeb.service.Sale;

import com.veloProWeb.exceptions.customer.CustomerNotFoundException;
import com.veloProWeb.model.dto.SaleRequestDTO;
import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.model.entity.Sale.Sale;
import com.veloProWeb.model.Enum.PaymentMethod;
import com.veloProWeb.repository.Sale.SaleRepo;
import com.veloProWeb.service.customer.CustomerService;
import com.veloProWeb.service.customer.TicketHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

    @InjectMocks private SaleService saleService;
    @Mock private SaleRepo saleRepo;
    @Mock private TicketHistoryService ticketService;
    @Mock private CustomerService customerService;
    private SaleRequestDTO saleRequestDTO;
    private Customer customer;
    private Sale sale;

    @BeforeEach
    void setUp(){
        sale = new Sale();
        sale.setId(1L);
        sale.setDate(LocalDate.now());
        sale.setPaymentMethod(PaymentMethod.EFECTIVO);
        sale.setDocument("BO001");
        sale.setComment("Venta realizada");
        sale.setDiscount(0);
        sale.setTax(114);
        sale.setTotalSale(714);
        sale.setStatus(true);

        saleRequestDTO = new SaleRequestDTO();
        saleRequestDTO.setId(1L);
        saleRequestDTO.setDate(LocalDate.now());
        saleRequestDTO.setTax(114);
        saleRequestDTO.setTotal(714);
        saleRequestDTO.setDiscount(0);
        saleRequestDTO.setNumberDocument(340);
        saleRequestDTO.setDetailList(new ArrayList<>());

        customer = new Customer();
        customer.setId(2L);
    }

    //Prueba para crear uan nueva venta de diferentes modos de pago
    @Test
    public void createSale_validCashPayment() {
        saleRequestDTO.setPaymentMethod(PaymentMethod.EFECTIVO);
        saleRequestDTO.setComment("1000");
        saleRequestDTO.setIdCustomer(null);

        Sale createdSale = saleService.createSale(saleRequestDTO);
        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
        verify(saleRepo).save(saleCaptor.capture());
        Sale savedSale = saleCaptor.getValue();
        assertEquals(PaymentMethod.EFECTIVO, savedSale.getPaymentMethod());
        assertEquals("BO_340", savedSale.getDocument());
        assertEquals("Efectivo: $1000", savedSale.getComment());
        assertNull(savedSale.getCustomer());
        assertTrue(savedSale.isStatus());
    }
    @Test
    public void createSale_validDebitPayment() {
        saleRequestDTO.setPaymentMethod(PaymentMethod.DEBITO);
        saleRequestDTO.setComment("1233424");
        saleRequestDTO.setIdCustomer(null);

        Sale createdSale = saleService.createSale(saleRequestDTO);
        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
        verify(saleRepo).save(saleCaptor.capture());
        Sale savedSale = saleCaptor.getValue();
        assertEquals(PaymentMethod.DEBITO, savedSale.getPaymentMethod());
        assertEquals("BO_340", savedSale.getDocument());
        assertEquals("Comprobante: n°1233424", savedSale.getComment());
        assertNull(savedSale.getCustomer());
        assertTrue(savedSale.isStatus());
    }
    @Test
    public void createSale_validCreditPayment() {
        saleRequestDTO.setPaymentMethod(PaymentMethod.CREDITO);
        saleRequestDTO.setComment("1233424");
        saleRequestDTO.setIdCustomer(null);

        Sale createdSale = saleService.createSale(saleRequestDTO);
        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
        verify(saleRepo).save(saleCaptor.capture());
        Sale savedSale = saleCaptor.getValue();
        assertEquals(PaymentMethod.CREDITO, savedSale.getPaymentMethod());
        assertEquals("BO_340", savedSale.getDocument());
        assertEquals("Comprobante: n°1233424", savedSale.getComment());
        assertNull(savedSale.getCustomer());
        assertTrue(savedSale.isStatus());
    }
    @Test
    public void createSale_validTransferPayment() {
        saleRequestDTO.setPaymentMethod(PaymentMethod.TRANSFERENCIA);
        saleRequestDTO.setComment("1233424");
        saleRequestDTO.setIdCustomer(null);

        Sale createdSale = saleService.createSale(saleRequestDTO);
        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
        verify(saleRepo).save(saleCaptor.capture());
        Sale savedSale = saleCaptor.getValue();
        assertEquals(PaymentMethod.TRANSFERENCIA, savedSale.getPaymentMethod());
        assertEquals("BO_340", savedSale.getDocument());
        assertEquals("Transferencia: n°1233424", savedSale.getComment());
        assertNull(savedSale.getCustomer());
        assertTrue(savedSale.isStatus());
    }
    @Test
    public void createSale_validLoanPayment() {
        saleRequestDTO.setPaymentMethod(PaymentMethod.PRESTAMO);
        saleRequestDTO.setComment(null);
        saleRequestDTO.setIdCustomer(2L);
        when(customerService.getCustomerById(saleRequestDTO.getIdCustomer())).thenReturn(customer);

        Sale createdSale = saleService.createSale(saleRequestDTO);
        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
        verify(saleRepo).save(saleCaptor.capture());
        Sale savedSale = saleCaptor.getValue();
        assertEquals(PaymentMethod.PRESTAMO, savedSale.getPaymentMethod());
        assertEquals("BO_340", savedSale.getDocument());
        assertNull(savedSale.getComment());
        assertEquals( 2L, savedSale.getCustomer().getId());
        assertTrue(savedSale.isStatus());
        verify(customerService).getCustomerById(saleRequestDTO.getIdCustomer());
        verify(customerService).updateTotalDebt(customer);
        verify(ticketService).addTicketToCustomer(customer, saleRequestDTO.getTotal());
    }
    @Test
    public void createSale_validMixPayment() {
        saleRequestDTO.setPaymentMethod(PaymentMethod.MIXTO);
        saleRequestDTO.setComment("1000");
        saleRequestDTO.setIdCustomer(2L);
        when(customerService.getCustomerById(saleRequestDTO.getIdCustomer())).thenReturn(customer);

        Sale createdSale = saleService.createSale(saleRequestDTO);
        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
        verify(saleRepo).save(saleCaptor.capture());
        Sale savedSale = saleCaptor.getValue();
        assertEquals(PaymentMethod.MIXTO, savedSale.getPaymentMethod());
        assertEquals("BO_340", savedSale.getDocument());
        assertEquals( "Abono inicial: $1000", savedSale.getComment());
        assertEquals( 2L, savedSale.getCustomer().getId());
        assertTrue(savedSale.isStatus());
        verify(customerService).getCustomerById(saleRequestDTO.getIdCustomer());
        verify(customerService).updateTotalDebt(customer);
        verify(ticketService).addTicketToCustomer(customer, saleRequestDTO.getTotal());
    }
    @Test
    public void createSale_customerNotFound() {
        saleRequestDTO.setPaymentMethod(PaymentMethod.PRESTAMO);
        saleRequestDTO.setComment(null);
        saleRequestDTO.setIdCustomer(4L);
        when(customerService.getCustomerById(saleRequestDTO.getIdCustomer()))
                .thenThrow(new CustomerNotFoundException("Cliente no encontrado"));
        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> saleService.createSale(saleRequestDTO));

        assertEquals("Cliente no encontrado", exception.getMessage());
        verify(saleRepo , never()).save(any(Sale.class));
        verify(customerService).getCustomerById(saleRequestDTO.getIdCustomer());
    }

    //Prueba para obtener el total de ventas realizadas
    @Test
    public void totalSales_valid(){
        when(saleRepo.count()).thenReturn(1L);
        Long totalSale =  saleService.totalSales();
        verify(saleRepo).count();
        assertEquals(1L, totalSale);
    }

    //Prueba para obtener todas las ventas registradas
    @Test
    public void getAllSale_valid(){
        when(saleRepo.findAll()).thenReturn(Collections.singletonList(sale));

        List<SaleRequestDTO> result = saleService.getAllSale();
        verify(saleRepo, times(1)).findAll();
        assertNotNull(result);
        assertEquals(1, result.size());

        SaleRequestDTO dto = result.getFirst();
        assertEquals(sale.getId(), dto.getId());
        assertEquals(sale.getDate(), dto.getDate());
        assertEquals(sale.getPaymentMethod(), dto.getPaymentMethod());
    }

    //Prueba para obtener una venta por ID
    @Test
    public void getSaleById_valid(){
        when(saleRepo.findById(1L)).thenReturn(Optional.of(sale));
        saleService.getSaleById(1L);
        verify(saleRepo).findById(1L);
    }
    @Test
    public void getSaleById_validNull(){
        when(saleRepo.findById(2L)).thenReturn(Optional.empty());
        saleService.getSaleById(2L);
        verify(saleRepo).findById(2L);
    }
}
