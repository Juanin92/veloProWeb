package com.veloproweb.service.inventory;

import com.veloproweb.mapper.KardexMapper;
import com.veloproweb.model.dto.inventory.KardexResponseDTO;
import com.veloproweb.model.entity.inventory.Kardex;
import com.veloproweb.model.entity.user.User;
import com.veloproweb.model.entity.product.Product;
import com.veloproweb.model.Enum.MovementsType;
import com.veloproweb.repository.KardexRepo;
import com.veloproweb.service.user.interfaces.IUserService;
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
class KardexServiceTest {
    @InjectMocks private KardexService kardexService;
    @Mock private KardexRepo kardexRepo;
    @Mock private IUserService userService;
    @Mock private KardexMapper mapper;

    //Prueba para crear un registro
    @Test
    void addKardex_valid(){
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("usernameTest");
        Product product = Product.builder().id(1L).description("Product Test").build();
        User user = User.builder().id(1L).username("usernameTest").build();
        when(userService.getUserByUsername("usernameTest")).thenReturn(user);
        Kardex mapped = Kardex.builder().movementsType(MovementsType.ENTRADA).date(LocalDate.now()).product(product)
                .user(user).comment("Test kardex").build();
        when(mapper.toEntity(product, 2, "Test kardex", MovementsType.ENTRADA, user)).thenReturn(mapped);

        kardexService.addKardex( userDetails, product, 2, "Test kardex", MovementsType.ENTRADA);

        ArgumentCaptor<Kardex> kardexArgumentCaptor = ArgumentCaptor.forClass(Kardex.class);
        verify(userService, times(1)).getUserByUsername("usernameTest");
        verify(mapper, times(1)).toEntity(product, 2, "Test kardex",
                MovementsType.ENTRADA, user);
        verify(kardexRepo, times(1)).save(kardexArgumentCaptor.capture());

        Kardex result = kardexArgumentCaptor.getValue();
        assertEquals(LocalDate.now(), result.getDate());
        assertEquals("Test kardex", result.getComment());
        assertEquals(product, result.getProduct());
        assertEquals(user, result.getUser());
    }

    //Prueba para obtener todos los registros
    @Test
    void getAll_valid(){
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

    //Prueba para obtener un registro de movimiento de un producto desde una fecha
    @Test
    void getProductMovementsSinceDate_valid(){
        LocalDate days = LocalDate.now().minusDays(90);
        Product product = Product.builder().id(1L).description("TV").build();
        Kardex entry = Kardex.builder().product(product).movementsType(MovementsType.ENTRADA)
                .date(LocalDate.now().minusDays(95)).build();
        Kardex adjust = Kardex.builder().product(product).movementsType(MovementsType.AJUSTE)
                .date(LocalDate.now().minusDays(95)).build();
        Kardex exit = Kardex.builder().product(product).movementsType(MovementsType.SALIDA)
                .date(LocalDate.now().minusDays(95)).build();
        when(kardexRepo.findByProductAndDateAfter(product, days)).thenReturn(List.of(entry, adjust, exit));

        List<Kardex> result = kardexService.getProductMovementsSinceDate(product, days);

        verify(kardexRepo, times(1)).findByProductAndDateAfter(product, days);

        assertEquals(3, result.size());
        assertEquals( entry, result.getFirst());
    }
}
