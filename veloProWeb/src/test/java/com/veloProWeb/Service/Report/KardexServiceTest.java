package com.veloProWeb.Service.Report;

import com.veloProWeb.Model.DTO.KardexRequestDTO;
import com.veloProWeb.Model.Entity.Kardex;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Enum.MovementsType;
import com.veloProWeb.Repository.KardexRepo;
import com.veloProWeb.Service.Product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KardexServiceTest {
    @InjectMocks private KardexService kardexService;
    @Mock private KardexRepo kardexRepo;
    @Mock private ProductService productService;
    private Kardex kardex;
    private KardexRequestDTO dto;
    private Product product;

    @BeforeEach
    void setUp(){
        kardex = new Kardex();
        kardex.setId(1L);
        kardex.setComment("Prueba");
        kardex.setDate(LocalDate.now());
        kardex.setMovementsType(MovementsType.ENTRADA);
        kardex.setStock(10);
        kardex.setPrice(2000);
        kardex.setQuantity(2);

        dto = new KardexRequestDTO();
        dto.setComment("Prueba");
        dto.setMovementsType(MovementsType.ENTRADA);
        dto.setIdProduct(1L);
        dto.setQuantity(2);

        product = new Product();
        product.setId(1L);
        product.setStock(10);
        product.setBuyPrice(2000);
    }

    //Prueba para crear un registro
    @Test
    public void addKardex_valid(){
        kardex.setProduct(product);
        kardex.setId(null);
        when(productService.getProductById(1L)).thenReturn(product);
        kardexService.addKardex(dto);

        verify(productService).getProductById(1L);
        verify(kardexRepo).save(kardex);
    }

    //Prueba para obtener todos los registros
    @Test
    public void getAll_valid(){
        when(kardexRepo.findAll()).thenReturn(Collections.singletonList(kardex));
        List<Kardex> result = kardexService.getAll();
        verify(kardexRepo).findAll();
        assertEquals(kardex.getComment(), result.getFirst().getComment());
    }
}
