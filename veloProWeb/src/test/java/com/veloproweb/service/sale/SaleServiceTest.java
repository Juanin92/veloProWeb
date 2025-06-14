package com.veloproweb.service.sale;

import com.veloproweb.exceptions.sale.SaleNotFoundException;
import com.veloproweb.mapper.SaleMapper;
import com.veloproweb.model.dto.sale.SaleDetailRequestDTO;
import com.veloproweb.model.dto.sale.SaleRequestDTO;
import com.veloproweb.model.dto.sale.SaleResponseDTO;
import com.veloproweb.model.entity.Sale.SaleDetail;
import com.veloproweb.model.entity.customer.Customer;
import com.veloproweb.model.entity.Sale.Sale;
import com.veloproweb.model.Enum.PaymentMethod;
import com.veloproweb.model.entity.product.Product;
import com.veloproweb.repository.Sale.SaleRepo;
import com.veloproweb.service.sale.Interface.ISaleEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

    @InjectMocks private SaleService saleService;
    @Mock private SaleRepo saleRepo;
    @Mock private ISaleEventService eventService;
    @Spy private SaleMapper mapper;
    private Sale sale;
    private Customer customer;

    @BeforeEach
    void setUp(){
        sale = Sale.builder().id(1L).document("0001").build();
        customer = Customer.builder().id(1L).name("John").surname("Doe").build();
    }

    //Prueba para crear uan nueva venta de diferentes modos de pago
    @Test
    public void createSale_cashPayment() {
        SaleDetailRequestDTO saleDetailRequestDTO = SaleDetailRequestDTO.builder().idProduct(1L).quantity(10).build();
        SaleRequestDTO saleRequestDTO = SaleRequestDTO.builder().idCustomer(null).paymentMethod(PaymentMethod.EFECTIVO)
                .tax(100).total(1000).discount(0).comment("1000").detailList(List.of(saleDetailRequestDTO)).build();

        when(saleRepo.findLastCreated()).thenReturn(Optional.of(sale));
        when(eventService.needsCustomer(saleRequestDTO.getPaymentMethod(), null)).thenReturn(null);

        Sale saleMapped = mapper.toSaleEntity(saleRequestDTO, "0001", saleRequestDTO.getPaymentMethod(),
                String.format("Efectivo: $%s", saleRequestDTO.getComment()), null);

        Sale saleCreated = saleService.createSale(saleRequestDTO);

        verify(saleRepo, times(1)).findLastCreated();
        verify(eventService, times(1)).needsCustomer(saleRequestDTO.getPaymentMethod(),
                null);
        verify(saleRepo, times(1)).save(saleCreated);

        assertEquals(saleMapped, saleCreated);
        assertEquals("BO-0625-0001", saleCreated.getDocument());
        assertEquals("Efectivo: $1000", saleCreated.getComment());
        assertEquals(PaymentMethod.EFECTIVO, saleCreated.getPaymentMethod());
        assertTrue(saleCreated.isStatus());
        assertNull(saleCreated.getCustomer());
    }
    @Test
    public void createSale_debitPayment() {
        SaleDetailRequestDTO saleDetailRequestDTO = SaleDetailRequestDTO.builder().idProduct(1L).quantity(10).build();
        SaleRequestDTO saleRequestDTO = SaleRequestDTO.builder().idCustomer(null).paymentMethod(PaymentMethod.DEBITO)
                .tax(100).total(1000).discount(0).comment("ticket number").detailList(List.of(saleDetailRequestDTO)).build();

        when(saleRepo.findLastCreated()).thenReturn(Optional.of(sale));
        when(eventService.needsCustomer(saleRequestDTO.getPaymentMethod(), null)).thenReturn(null);

        Sale saleMapped = mapper.toSaleEntity(saleRequestDTO, "0001", saleRequestDTO.getPaymentMethod(),
                String.format("Comprobante: n°%s", saleRequestDTO.getComment()), null);

        Sale saleCreated = saleService.createSale(saleRequestDTO);

        verify(saleRepo, times(1)).findLastCreated();
        verify(eventService, times(1)).needsCustomer(saleRequestDTO.getPaymentMethod(),
                null);
        verify(saleRepo, times(1)).save(saleCreated);

        assertEquals(saleMapped, saleCreated);
        assertEquals("BO-0625-0001", saleCreated.getDocument());
        assertEquals("Comprobante: n°ticket number", saleCreated.getComment());
        assertEquals(PaymentMethod.DEBITO, saleCreated.getPaymentMethod());
        assertTrue(saleCreated.isStatus());
        assertNull(saleCreated.getCustomer());
    }
    @Test
    public void createSale_creditPayment() {
        SaleDetailRequestDTO saleDetailRequestDTO = SaleDetailRequestDTO.builder().idProduct(1L).quantity(10).build();
        SaleRequestDTO saleRequestDTO = SaleRequestDTO.builder().idCustomer(null).paymentMethod(PaymentMethod.CREDITO)
                .tax(100).total(1000).discount(0).comment("ticket number").detailList(List.of(saleDetailRequestDTO)).build();

        when(saleRepo.findLastCreated()).thenReturn(Optional.of(sale));
        when(eventService.needsCustomer(saleRequestDTO.getPaymentMethod(), null)).thenReturn(null);

        Sale saleMapped = mapper.toSaleEntity(saleRequestDTO, "0001", saleRequestDTO.getPaymentMethod(),
                String.format("Comprobante: n°%s", saleRequestDTO.getComment()), null);

        Sale saleCreated = saleService.createSale(saleRequestDTO);

        verify(saleRepo, times(1)).findLastCreated();
        verify(eventService, times(1)).needsCustomer(saleRequestDTO.getPaymentMethod(),
                null);
        verify(saleRepo, times(1)).save(saleCreated);

        assertEquals(saleMapped, saleCreated);
        assertEquals("BO-0625-0001", saleCreated.getDocument());
        assertEquals("Comprobante: n°ticket number", saleCreated.getComment());
        assertEquals(PaymentMethod.CREDITO, saleCreated.getPaymentMethod());
        assertTrue(saleCreated.isStatus());
        assertNull(saleCreated.getCustomer());
    }
    @Test
    public void createSale_transferPayment() {
        SaleDetailRequestDTO saleDetailRequestDTO = SaleDetailRequestDTO.builder().idProduct(1L).quantity(10).build();
        SaleRequestDTO saleRequestDTO = SaleRequestDTO.builder().idCustomer(null).tax(100).total(1000).discount(0)
                .paymentMethod(PaymentMethod.TRANSFERENCIA).comment("transfer number")
                .detailList(List.of(saleDetailRequestDTO)).build();

        when(saleRepo.findLastCreated()).thenReturn(Optional.of(sale));
        when(eventService.needsCustomer(saleRequestDTO.getPaymentMethod(), null)).thenReturn(null);

        Sale saleMapped = mapper.toSaleEntity(saleRequestDTO, "0001", saleRequestDTO.getPaymentMethod(),
                String.format("Transferencia: n°%s", saleRequestDTO.getComment()), null);

        Sale saleCreated = saleService.createSale(saleRequestDTO);

        verify(saleRepo, times(1)).findLastCreated();
        verify(eventService, times(1)).needsCustomer(saleRequestDTO.getPaymentMethod(),
                null);
        verify(saleRepo, times(1)).save(saleCreated);

        assertEquals(saleMapped, saleCreated);
        assertEquals("BO-0625-0001", saleCreated.getDocument());
        assertEquals("Transferencia: n°transfer number", saleCreated.getComment());
        assertEquals(PaymentMethod.TRANSFERENCIA, saleCreated.getPaymentMethod());
        assertTrue(saleCreated.isStatus());
        assertNull(saleCreated.getCustomer());
    }
    @Test
    public void createSale_loanPayment() {
        SaleDetailRequestDTO saleDetailRequestDTO = SaleDetailRequestDTO.builder().idProduct(1L).quantity(10).build();
        SaleRequestDTO saleRequestDTO = SaleRequestDTO.builder().idCustomer(1L).paymentMethod(PaymentMethod.PRESTAMO)
                .tax(100).total(1000).discount(0).comment("ticket number").detailList(List.of(saleDetailRequestDTO)).build();

        when(saleRepo.findLastCreated()).thenReturn(Optional.of(sale));
        when(eventService.needsCustomer(saleRequestDTO.getPaymentMethod(), 1L)).thenReturn(customer);

        Sale saleMapped = mapper.toSaleEntity(saleRequestDTO, "0001", saleRequestDTO.getPaymentMethod(),
                null, customer);

        doNothing().when(eventService).createSaleLoanPaymentEvent(customer, saleRequestDTO.getTotal());

        Sale saleCreated = saleService.createSale(saleRequestDTO);

        verify(saleRepo, times(1)).findLastCreated();
        verify(eventService, times(1)).needsCustomer(saleRequestDTO.getPaymentMethod(),
                1L);
        verify(eventService, times(1)).createSaleLoanPaymentEvent(customer,
                saleRequestDTO.getTotal());
        verify(saleRepo, times(1)).save(saleCreated);

        assertEquals(saleMapped, saleCreated);
        assertEquals("BO-0625-0001", saleCreated.getDocument());
        assertNull(saleCreated.getComment());
        assertEquals(PaymentMethod.PRESTAMO, saleCreated.getPaymentMethod());
        assertTrue(saleCreated.isStatus());
        assertEquals(customer, saleCreated.getCustomer());
    }
    @Test
    public void createSale_mixedPayment() {
        SaleDetailRequestDTO saleDetailRequestDTO = SaleDetailRequestDTO.builder().idProduct(1L).quantity(10).build();
        SaleRequestDTO saleRequestDTO = SaleRequestDTO.builder().idCustomer(1L).paymentMethod(PaymentMethod.MIXTO)
                .tax(100).total(1000).discount(0).comment("ticket number").detailList(List.of(saleDetailRequestDTO)).build();

        when(saleRepo.findLastCreated()).thenReturn(Optional.of(sale));
        when(eventService.needsCustomer(saleRequestDTO.getPaymentMethod(), 1L)).thenReturn(customer);

        Sale saleMapped = mapper.toSaleEntity(saleRequestDTO, "0001", saleRequestDTO.getPaymentMethod(),
                String.format("Abono inicial: $%s", saleRequestDTO.getComment()), customer);

        doNothing().when(eventService).createSaleMixPaymentEvent(customer, saleRequestDTO.getTotal());

        Sale saleCreated = saleService.createSale(saleRequestDTO);

        verify(saleRepo, times(1)).findLastCreated();
        verify(eventService, times(1)).needsCustomer(saleRequestDTO.getPaymentMethod(),
                1L);
        verify(eventService, times(1)).createSaleMixPaymentEvent(customer,
                saleRequestDTO.getTotal());
        verify(saleRepo, times(1)).save(saleCreated);

        assertEquals(saleMapped, saleCreated);
        assertEquals("BO-0625-0001", saleCreated.getDocument());
        assertEquals("Abono inicial: $ticket number", saleCreated.getComment());
        assertEquals(PaymentMethod.MIXTO, saleCreated.getPaymentMethod());
        assertTrue(saleCreated.isStatus());
        assertEquals(customer, saleCreated.getCustomer());
    }

    //Prueba para obtener el total de ventas realizadas
    @Test
    public void totalSales(){
        when(saleRepo.count()).thenReturn(1L);
        Long totalSale =  saleService.totalSales();
        verify(saleRepo).count();
        assertEquals(1L, totalSale);
    }

    //Prueba para obtener todas las ventas registradas
    @Test
    public void getAllSales(){
        Sale backUp = Sale.builder().document("Test-Doc").customer(customer).build();
        SaleDetail saleDetail = SaleDetail.builder().id(1L).sale(backUp).dispatch(null)
                .product(Product.builder().description("Product Description").build()).build();
        backUp.setSaleDetails(List.of(saleDetail));
        when(saleRepo.findAll()).thenReturn(List.of(backUp));

        List<SaleResponseDTO> result = saleService.getAllSales();

        verify(saleRepo, times(1)).findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Product Description", result.getFirst().getSaleDetails().getFirst()
                .getDescriptionProduct());
        assertEquals("John Doe", result.getFirst().getCustomer());
    }

    //Prueba para obtener una venta por ID
    @Test
    public void getSaleById(){
        when(saleRepo.findById(1L)).thenReturn(Optional.of(sale));

        Sale result = saleService.getSaleById(1L);
        verify(saleRepo).findById(1L);
        assertEquals("0001", result.getDocument());
    }
    @Test
    public void getSaleById_NotFoundException(){
        when(saleRepo.findById(2L)).thenReturn(Optional.empty());

        SaleNotFoundException e = assertThrows(SaleNotFoundException.class, () -> saleService.getSaleById(2L));

        verify(saleRepo, times(1)).findById(2L);
        assertEquals("Venta no encontrada", e.getMessage());
    }
}
