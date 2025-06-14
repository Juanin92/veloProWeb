package com.veloproweb.service.product;

import com.veloproweb.exceptions.product.BrandAlreadyExistsException;
import com.veloproweb.model.entity.product.BrandProduct;
import com.veloproweb.repository.product.BrandProductRepo;
import com.veloproweb.validation.BrandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {

    @InjectMocks private BrandService brandService;
    @Mock private BrandProductRepo brandRepo;
    @Mock private BrandValidator validator;
    private BrandProduct brand, brand2, brand3, existingBrand;

    @BeforeEach
    void setUp(){
        brand = BrandProduct.builder().id(1L).name("Asus").build();
        brand2 = BrandProduct.builder().id(2L).name("Apple").build();
        brand3 = BrandProduct.builder().id(3L).name("Sony").build();
        existingBrand = BrandProduct.builder().id(6L).name("Samsung").build();
    }

    //Prueba para crear una nueva marca
    @Test
    public void save_valid(){
        when(brandRepo.findByName("Asus")).thenReturn(Optional.empty());
        doNothing().when(validator).validateBrandDoesNotExist(null);

        brandService.save(brand);

        verify(brandRepo, times(1)).findByName("Asus");
        verify(brandRepo, times(1)).save(brand);
        assertEquals("Asus", brand.getName());
    }
    @Test
    public void save_invalidExistingBrand(){
        when(brandRepo.findByName("Samsung")).thenReturn(Optional.of(existingBrand));
        doThrow(new BrandAlreadyExistsException("Nombre Existente: Hay registro de esta marca.")).when(validator)
                .validateBrandDoesNotExist(existingBrand);

        BrandAlreadyExistsException exception = assertThrows(BrandAlreadyExistsException.class,
                () -> brandService.save(existingBrand));
        verify(brandRepo, times(1)).findByName("Samsung");
        verify(brandRepo, never()).save(existingBrand);

        assertEquals("Nombre Existente: Hay registro de esta marca.", exception.getMessage());
    }

    //Prueba para obtener todas las marcas
    @Test
    public void getAll_valid(){
        List<BrandProduct> brands = List.of(brand2, brand, existingBrand, brand3);
        when(brandRepo.findAllOrderByNameAsc()).thenReturn(brands);

        List<BrandProduct> result = brandService.getAll();

        verify(brandRepo).findAllOrderByNameAsc();

        assertEquals(brands.size(), result.size());
        assertEquals(List.of(brand2, brand, existingBrand, brand3), result);
    }
}
