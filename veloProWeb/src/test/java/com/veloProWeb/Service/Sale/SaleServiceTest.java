package com.veloProWeb.Service.Sale;

import com.veloProWeb.Model.DTO.SaleRequestDTO;
import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Enum.PaymentMethod;
import com.veloProWeb.Repository.Sale.SaleRepo;
import com.veloProWeb.Service.Customer.CustomerService;
import com.veloProWeb.Service.Customer.TicketHistoryService;
import com.veloProWeb.Service.SaleService.SaleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;

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

    @BeforeEach
    void setUp(){
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
        verify(ticketService).AddTicketToCustomer(customer, (long) saleRequestDTO.getNumberDocument(), saleRequestDTO.getTotal(), saleRequestDTO.getDate());
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
        verify(ticketService).AddTicketToCustomer(customer, (long) saleRequestDTO.getNumberDocument(), saleRequestDTO.getTotal(), saleRequestDTO.getDate());
    }
    @Test
    public void createSale_customerNotFound() {
        saleRequestDTO.setPaymentMethod(PaymentMethod.PRESTAMO);
        saleRequestDTO.setComment(null);
        saleRequestDTO.setIdCustomer(4L);
        when(customerService.getCustomerById(saleRequestDTO.getIdCustomer())).thenThrow(new IllegalArgumentException("Cliente no encontrado"));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,() -> saleService.createSale(saleRequestDTO));

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
}
