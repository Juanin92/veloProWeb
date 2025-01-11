package com.veloProWeb.Service.Product;

import com.veloProWeb.Model.Entity.Product.CategoryProduct;
import com.veloProWeb.Repository.Product.CategoryProductRepo;
import com.veloProWeb.Utils.HelperService;
import com.veloProWeb.Validation.CategoriesValidator;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks private CategoryService categoryService;
    @Mock private CategoryProductRepo categoryProductRepo;
    @Mock private CategoriesValidator validator;
    @Mock private HelperService helperService;
    private CategoryProduct category;
    private CategoryProduct existingCategory;

    @BeforeEach
    void setUp(){
        category = new CategoryProduct();
        category.setName("combustible");
        existingCategory = new CategoryProduct();
        existingCategory.setName("Comida");
    }

    //Prueba para crear una nueva marca
    @Test
    public void save_valid(){
        doNothing().when(validator).validateCategory("combustible");
        when(helperService.capitalize("combustible")).thenReturn("Combustible");
        when(categoryProductRepo.findByName("Combustible")).thenReturn(Optional.empty());
        categoryService.save(category);

        verify(validator).validateCategory("combustible");
        verify(categoryProductRepo).findByName("Combustible");
        verify(categoryProductRepo).save(category);
        assertEquals("Combustible", category.getName());
    }
    @Test
    public void save_invalidExistingCategory(){
        category.setName("Comida");
        doNothing().when(validator).validateCategory("Comida");
        when(categoryProductRepo.findByName("Comida")).thenReturn(Optional.of(existingCategory));
        when(helperService.capitalize("Comida")).thenReturn("Comida");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.save(category);
        });

        assertEquals("Nombre Existente: Hay registro de esta categoría.", exception.getMessage());
        verify(validator).validateCategory("Comida");
        verify(categoryProductRepo).findByName("Comida");
        verify(categoryProductRepo, never()).save(category);
    }

    //Prueba para obtener todas las marcas
    @Test
    public void getAll_valid(){
        categoryService.getAll();
        verify(categoryProductRepo).findAll();
    }

}
