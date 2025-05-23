package com.veloProWeb.service.Report;

import com.veloProWeb.mapper.KardexMapper;
import com.veloProWeb.model.dto.KardexResponseDTO;
import com.veloProWeb.model.entity.Kardex;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.repository.KardexRepo;
import com.veloProWeb.service.User.Interface.IUserService;
import com.veloProWeb.service.User.AlertService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KardexServiceTest {
    @InjectMocks private KardexService kardexService;
    @Mock private KardexRepo kardexRepo;
    @Mock private IUserService userService;
    @Mock private AlertService alertService;
    @Mock private KardexMapper mapper;

    //Prueba para crear un registro
    @Test
    public void addKardex_valid(){
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("usernameTest");
        Product product = Product.builder().id(1L).description("Product Test").build();
        User user = User.builder().id(1L).username("usernameTest").build();
        when(userService.getUserWithUsername("usernameTest")).thenReturn(user);

        kardexService.addKardex( userDetails, product, 2, "Test kardex", MovementsType.ENTRADA);

        ArgumentCaptor<Kardex> kardexArgumentCaptor = ArgumentCaptor.forClass(Kardex.class);
        verify(kardexRepo, times(1)).save(kardexArgumentCaptor.capture());
        verify(userService, times(1)).getUserWithUsername("usernameTest");

        Kardex result = kardexArgumentCaptor.getValue();
        assertEquals(LocalDate.now(), result.getDate());
        assertEquals("Test kardex", result.getComment());
        assertEquals(product, result.getProduct());
        assertEquals(user, result.getUser());
    }

    //Prueba para obtener todos los registros
    @Test
    public void getAll_valid(){
        Kardex kardex = Kardex.builder().id(1L).movementsType(MovementsType.AJUSTE)
                .user(User.builder().name("John").surname("Doe").build()).build();
        Kardex kardex2 = Kardex.builder().id(2L).movementsType(MovementsType.ENTRADA).build();
        when(kardexRepo.findAll()).thenReturn(List.of(kardex, kardex2));
        KardexResponseDTO dto = KardexResponseDTO.builder().user("John Doe").movementsType(MovementsType.AJUSTE).build();
        KardexResponseDTO dto2 = KardexResponseDTO.builder().movementsType(MovementsType.ENTRADA).build();
        when(mapper.toResponseDTO(kardex)).thenReturn(dto);
        when(mapper.toResponseDTO(kardex2)).thenReturn(dto2);

        List<KardexResponseDTO> result = kardexService.getAll();
        verify(kardexRepo, times(1)).findAll();

        assertEquals(result.size(), List.of(kardex, kardex2).size());
        assertEquals(MovementsType.AJUSTE, result.getFirst().getMovementsType());
        assertEquals("John Doe", result.getFirst().getUser());
        assertEquals(MovementsType.ENTRADA, result.getLast().getMovementsType());
    }

    //Prueba para validar las ventas bajas de un producto
    @Test
    public void checkLowSales_valid(){
        LocalDate days = LocalDate.now().minusDays(90);
        Product product = Product.builder().id(1L).description("Product test").stock(10).buyPrice(2000).build();
        Kardex kardexEntry = Kardex.builder().id(1L).product(product).quantity(20).movementsType(MovementsType.ENTRADA)
                .date(LocalDate.now().minusDays(95)).stock(10).price(2000).build();
        Kardex kardexExit = Kardex.builder().id(1L).product(product).quantity(2).movementsType(MovementsType.SALIDA)
                .date(LocalDate.now().minusDays(85)).stock(19).price(2000).build();
        when(kardexRepo.findByProductAndDateAfter(product, days)).thenReturn(List.of(kardexEntry, kardexExit));
        String description = String.format("Producto sin Ventas (+ 90 días) -> %s", product.getDescription());
        when(alertService.isAlertActive(product, description)).thenReturn(false);

        kardexService.checkLowSales(product);

        verify(kardexRepo, times(1)).findByProductAndDateAfter(product, days);
        verify(alertService, times(1)).isAlertActive(product, description);
        verify(alertService, times(1)).createAlert(product, description);
    }
    @Test
    public void checkLowSales_noLowSalesCondition() {
        LocalDate days = LocalDate.now().minusDays(90);
        Product product = Product.builder().build();
        Kardex kardexEntry = Kardex.builder().id(1L).product(product).quantity(20).movementsType(MovementsType.ENTRADA)
                .date(LocalDate.now().minusDays(95)).stock(10).price(2000).build();
        Kardex kardexExit = Kardex.builder().id(1L).product(product).quantity(19).movementsType(MovementsType.SALIDA)
                .date(LocalDate.now().minusDays(85)).stock(19).price(2000).build();
        when(kardexRepo.findByProductAndDateAfter(product, days)).thenReturn(List.of(kardexEntry, kardexExit));

        kardexService.checkLowSales(product);

        verify(kardexRepo, times(1)).findByProductAndDateAfter(product, days);
        verifyNoInteractions(alertService);
    }
    @Test
    public void checkLowSales_alertAlreadyExists() {
        LocalDate days = LocalDate.now().minusDays(90);
        Product product = Product.builder().build();
        Kardex kardexEntry = Kardex.builder().id(1L).product(product).quantity(20).movementsType(MovementsType.ENTRADA)
                .date(LocalDate.now().minusDays(95)).stock(10).price(2000).build();
        Kardex kardexExit = Kardex.builder().id(1L).product(product).quantity(2).movementsType(MovementsType.SALIDA)
                .date(LocalDate.now().minusDays(85)).stock(10).price(2000).build();
        String description = String.format("Producto sin Ventas (+ 90 días) -> %s", product.getDescription());
        when(kardexRepo.findByProductAndDateAfter(product, days)).thenReturn(List.of(kardexEntry, kardexExit));
        when(alertService.isAlertActive(product, description)).thenReturn(true);

        kardexService.checkLowSales(product);

        verify(kardexRepo, times(1)).findByProductAndDateAfter(product, days);
        verify(alertService, times(1)).isAlertActive(product, description);
        verify(alertService, never()).createAlert(any(), any());
    }
}
