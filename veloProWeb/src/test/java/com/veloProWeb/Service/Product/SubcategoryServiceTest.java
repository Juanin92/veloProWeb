package com.veloProWeb.Service.Product;

import com.veloProWeb.Model.Entity.Product.BrandProduct;
import com.veloProWeb.Model.Entity.Product.CategoryProduct;
import com.veloProWeb.Model.Entity.Product.SubcategoryProduct;
import com.veloProWeb.Repository.Product.SubcategoryProductRepo;
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
public class SubcategoryServiceTest {

    @InjectMocks private SubcategoryService subcategoryService;
    @Mock private SubcategoryProductRepo subcategoryProductRepo;
    @Mock private CategoriesValidator validator;
    private SubcategoryProduct subcategory;
    private SubcategoryProduct existingSubcategory;
    private CategoryProduct category;

    @BeforeEach
    void setUp(){
        category = new CategoryProduct();
        category.setName("Comida");
        category.setId(1L);
        subcategory = new SubcategoryProduct();
        subcategory.setName("Leche");
        existingSubcategory = new SubcategoryProduct();
        existingSubcategory.setName("Arroz");
    }

    //Prueba para crear una nueva marca
    @Test
    public void save_valid(){
        when(subcategoryProductRepo.findByNameAndCategoryId("Leche",1L)).thenReturn(Optional.empty());
        doNothing().when(validator).validateSubcategory("Leche");
        subcategoryService.save(subcategory, category);

        verify(validator).validateSubcategory("Leche");
        verify(subcategoryProductRepo).findByNameAndCategoryId("Leche", 1L);
        verify(subcategoryProductRepo).save(subcategory);
        assertEquals("Leche", subcategory.getName());
    }
    @Test
    public void save_invalidExistingSubcategory(){
        subcategory.setName("Arroz");
        when(subcategoryProductRepo.findByNameAndCategoryId("Arroz", 1L)).thenReturn(Optional.of(existingSubcategory));
        doNothing().when(validator).validateSubcategory("Arroz");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subcategoryService.save(subcategory, category);
        });

        assertEquals("Nombre Existente: Hay registro de esta Subcategoría en la Categoría " + category.getName() + " .", exception.getMessage());
        verify(validator).validateSubcategory("Arroz");
        verify(subcategoryProductRepo).findByNameAndCategoryId("Arroz", 1L);
        verify(subcategoryProductRepo, never()).save(subcategory);
    }
    @Test
    public void save_invalidNullCategory(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subcategoryService.save(subcategory, null);
        });

        assertEquals("Dede seleccionar una categoría.", exception.getMessage());
    }

    //Prueba para obtener todas las marcas
    @Test
    public void getAll_valid(){
        subcategoryService.getAll();
        verify(subcategoryProductRepo).findAll();
    }
}
