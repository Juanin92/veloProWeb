package com.veloProWeb.service.sale;

import com.veloProWeb.exceptions.sale.DispatchNotFoundException;
import com.veloProWeb.exceptions.sale.InvalidDispatchStatusException;
import com.veloProWeb.mapper.DispatchMapper;
import com.veloProWeb.model.Enum.DispatchStatus;
import com.veloProWeb.model.dto.DetailSaleDTO;
import com.veloProWeb.model.dto.sale.DispatchRequestDTO;
import com.veloProWeb.model.dto.sale.DispatchResponseDTO;
import com.veloProWeb.model.entity.Sale.Dispatch;
import com.veloProWeb.model.entity.Sale.SaleDetail;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.repository.Sale.DispatchRepo;
import com.veloProWeb.service.product.interfaces.IProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DispatchServiceTest {

    @InjectMocks private DispatchService dispatchService;
    @Mock private DispatchRepo dispatchRepo;
    @Mock private IProductService productService;
    @Spy private DispatchMapper mapper;

    @BeforeEach
    void setUp(){
    }

    //Prueba para obtener los registro de los despachos
    @Test
    public void getDispatches(){
        SaleDetail saleDetail = SaleDetail.builder().id(1L).product(Product.builder().id(1L).build()).quantity(10)
                .total(1000).build();
        Dispatch dispatch = Dispatch.builder().id(1L).trackingNumber("#2").status(DispatchStatus.PREPARING)
                .created(LocalDate.now()).deliveryDate(null).hasSale(false).saleDetails(List.of(saleDetail)).build();
        when(dispatchRepo.findByStatusNot(DispatchStatus.DELETED)).thenReturn(List.of(dispatch));

        DetailSaleDTO saleDTO = DetailSaleDTO.builder().id(1L).idProduct(1L).quantity(10).build();
        DispatchResponseDTO responseDTO = DispatchResponseDTO.builder().id(1L).trackingNumber("#2")
                .status(DispatchStatus.PREPARING).created(LocalDate.now()).deliveryDate(null).hasSale(false)
                .detailSaleDTOS(List.of(saleDTO)).build();
        when(mapper.toResponseDTO(dispatch)).thenReturn(responseDTO);

        List<DispatchResponseDTO> result = dispatchService.getDispatches();

        verify(dispatchRepo, times(1)).findByStatusNot(DispatchStatus.DELETED);
        verify(mapper, times(2)).toResponseDTO(dispatch);
        assertEquals(1, result.size());
        assertEquals(DispatchStatus.PREPARING, result.getFirst().getStatus());
    }

    //Prueba para crear registro de despacho
    @Test
    public void createDispatch(){
        DetailSaleDTO detailSaleDTO = DetailSaleDTO.builder().id(1L).idProduct(1L).quantity(1).build();
        DispatchRequestDTO dto = DispatchRequestDTO.builder().address("Test address").comment("test comment")
                .customer("John Doe").detailSaleDTOList(List.of(detailSaleDTO)).build();
        when(dispatchRepo.count()).thenReturn(1L);

        Dispatch dispatchMapped = mapper.toEntity(dto, 1L);
        when(mapper.toEntity(dto, 1L)).thenReturn(dispatchMapped);

        Dispatch dispatch = dispatchService.createDispatch(dto);

        verify(dispatchRepo, times(1)).count();
        verify(mapper, times(2)).toEntity(dto, 1L);
        verify(dispatchRepo, times(1)).save(dispatchMapped);

        assertEquals("#2", dispatch.getTrackingNumber());
        assertEquals(DispatchStatus.PREPARING, dispatch.getStatus());
        assertEquals("Test address", dispatch.getAddress());
        assertEquals("test comment", dispatch.getComment());
        assertEquals("John Doe", dispatch.getCustomer());
        assertNull(dispatch.getDeliveryDate());
        assertFalse(dispatch.isHasSale());
    }

    //Prueba para manejar los estados de un despacho
    @Test
    public void handleStatus_inRouteStatus(){
        Dispatch dispatch = Dispatch.builder().id(1L).status(DispatchStatus.PREPARING).build();
        when(dispatchRepo.findById(1L)).thenReturn(Optional.of(dispatch));

        ArgumentCaptor<Dispatch> dispatchCaptor = ArgumentCaptor.forClass(Dispatch.class);
        dispatchService.handleStatus(1L, DispatchStatus.IN_ROUTE);

        verify(dispatchRepo, times(1)).save(dispatchCaptor.capture());

        Dispatch result = dispatchCaptor.getValue();
        assertEquals(DispatchStatus.IN_ROUTE, result.getStatus());
    }
    @Test
    public void handleStatus_deletedStatus(){
        SaleDetail saleDetail = SaleDetail.builder().quantity(2).product(Product.builder().id(1L).build()).build();
        Dispatch dispatch = Dispatch.builder().id(1L).status(DispatchStatus.IN_ROUTE).saleDetails(List.of(saleDetail))
                .build();
        when(dispatchRepo.findById(1L)).thenReturn(Optional.of(dispatch));
        doNothing().when(productService).updateStockAndReserveDispatch(saleDetail.getProduct(),
                saleDetail.getQuantity(), false);

        ArgumentCaptor<Dispatch> dispatchCaptor = ArgumentCaptor.forClass(Dispatch.class);
        dispatchService.handleStatus(1L, DispatchStatus.DELETED);

        verify(productService, times(1)).updateStockAndReserveDispatch(saleDetail.getProduct(),
                saleDetail.getQuantity(), false);
        verify(dispatchRepo, times(1)).save(dispatchCaptor.capture());

        Dispatch result = dispatchCaptor.getValue();
        assertEquals(DispatchStatus.DELETED, result.getStatus());
    }
    @Test
    public void handleStatus_DispatchNotFoundException() {
        when(dispatchRepo.findById(2L)).thenReturn(Optional.empty());
        DispatchNotFoundException e = assertThrows(DispatchNotFoundException.class,
                () -> dispatchService.handleStatus(2L, DispatchStatus.IN_ROUTE));

        verify(dispatchRepo, times(1)).findById(2L);
        verify(dispatchRepo, never()).save(any(Dispatch.class));
        assertEquals("No se encontró el despacho.", e.getMessage());
    }
    @Test
    public void handleStatus_statusActionException() {
        Dispatch dispatch = Dispatch.builder().id(1L).status(DispatchStatus.PREPARING).build();
        when(dispatchRepo.findById(1L)).thenReturn(Optional.of(dispatch));

        InvalidDispatchStatusException e = assertThrows(InvalidDispatchStatusException.class,
                () -> dispatchService.handleStatus(1L, DispatchStatus.PREPARING));

        verify(dispatchRepo, times(1)).findById(1L);
        verify(dispatchRepo, never()).save(any(Dispatch.class));
        assertEquals("El estado escogido no es válido.", e.getMessage());
    }

    //Prueba para manejar despacho dejarlo como recibido
    @Test
    public void handleDispatchReceiveToSale_valid(){
        Dispatch dispatch = Dispatch.builder().id(1L).status(DispatchStatus.PREPARING).build();
        when(dispatchRepo.findById(1L)).thenReturn(Optional.of(dispatch));

        ArgumentCaptor<Dispatch> dispatchCaptor = ArgumentCaptor.forClass(Dispatch.class);
        dispatchService.handleDispatchReceiveToSale(1L);
        verify(dispatchRepo, times(1)).findById(1L);
        verify(dispatchRepo, times(1)).save(dispatchCaptor.capture());

        Dispatch result = dispatchCaptor.getValue();
        assertEquals(LocalDate.now(), result.getDeliveryDate());
        assertEquals(DispatchStatus.DELIVERED, result.getStatus());
    }

    //Prueba para obtener un despacho por su identificador
    @Test
    public void getDispatchById_valid(){
        Dispatch dispatch = Dispatch.builder().id(1L).status(DispatchStatus.PREPARING).build();
        when(dispatchRepo.findById(1L)).thenReturn(Optional.of(dispatch));

        Optional<Dispatch> result = dispatchService.getDispatchById(1L);

        verify(dispatchRepo, times(1)).findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(DispatchStatus.PREPARING, result.get().getStatus());
    }
}
