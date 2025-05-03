package com.veloProWeb.service.Product;

import com.veloProWeb.model.entity.Product.BrandProduct;
import com.veloProWeb.repository.Product.BrandProductRepo;
import com.veloProWeb.util.HelperService;
import com.veloProWeb.validation.CategoriesValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {

    @InjectMocks private BrandService brandService;
    @Mock private BrandProductRepo brandRepo;
    @Mock private CategoriesValidator validator;
    @Mock private HelperService helperService;
    private BrandProduct brand;
    private BrandProduct existingBrand;

    @BeforeEach
    void setUp(){
        brand = new BrandProduct();
        existingBrand = new BrandProduct();
        existingBrand.setName("Samsung");
    }

    //Prueba para crear una nueva marca
    @Test
    public void save_valid(){
        brand.setName("asus");
        doNothing().when(validator).validateBrand("asus");
        when(brandRepo.findByName("Asus")).thenReturn(Optional.empty());
        when(helperService.capitalize("asus")).thenReturn("Asus");
        brandService.save(brand);

        verify(validator).validateBrand("asus");
        verify(brandRepo).findByName("Asus");
        verify(brandRepo).save(brand);
        assertEquals("Asus", brand.getName());
    }
    @Test
    public void save_invalidExistingBrand(){
        brand.setName("Samsung");
        doNothing().when(validator).validateBrand("Samsung");
        when(brandRepo.findByName("Samsung")).thenReturn(Optional.of(existingBrand));
        when(helperService.capitalize("Samsung")).thenReturn("Samsung");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            brandService.save(brand);
        });

        assertEquals("Nombre Existente: Hay registro de este marca.", exception.getMessage());
        verify(validator).validateBrand("Samsung");
        verify(brandRepo).findByName("Samsung");
        verify(brandRepo, never()).save(brand);
    }

    //Prueba para obtener todas las marcas
    @Test
    public void getAll_valid(){
        brandService.getAll();
        verify(brandRepo).findAll();
    }
}
