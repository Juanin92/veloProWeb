package com.veloProWeb.Service.Product;

import com.veloProWeb.Model.Entity.Product.BrandProduct;
import com.veloProWeb.Model.Entity.Product.CategoryProduct;
import com.veloProWeb.Model.Entity.Product.SubcategoryProduct;
import com.veloProWeb.Repository.Product.CategoryProductRepo;
import com.veloProWeb.Repository.Product.SubcategoryProductRepo;
import com.veloProWeb.Utils.HelperService;
import com.veloProWeb.Validation.CategoriesValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubcategoryServiceTest {

    @InjectMocks private SubcategoryService subcategoryService;
    @Mock private SubcategoryProductRepo subcategoryProductRepo;
    @Mock private CategoryProductRepo categoryProductRepo;
    @Mock private CategoriesValidator validator;
    @Mock private HelperService helperService;
    private SubcategoryProduct subcategory;
    private SubcategoryProduct existingSubcategory;
    private SubcategoryProduct existingSubcategory2;
    private CategoryProduct category;

    @BeforeEach
    void setUp(){
        category = new CategoryProduct();
        category.setName("Comida");
        category.setId(1L);
        subcategory = new SubcategoryProduct();
        subcategory.setName("Leche");
        subcategory.setCategory(category);
        existingSubcategory = new SubcategoryProduct();
        existingSubcategory.setId(1L);
        existingSubcategory.setName("Leche");
        existingSubcategory.setCategory(category);
        existingSubcategory2 = new SubcategoryProduct();
        existingSubcategory2.setId(2L);
        existingSubcategory2.setName("Arroz");
        existingSubcategory2.setCategory(category);
    }

    //Prueba para crear una nueva subcategoría
    @Test
    public void save_valid(){
        doNothing().when(validator).validateSubcategory("Leche");
        when(subcategoryProductRepo.findByNameAndCategoryId("Leche",1L)).thenReturn(Optional.empty());
        when(helperService.capitalize("Leche")).thenReturn("Leche");
        subcategoryService.save(subcategory);

        verify(validator).validateSubcategory("Leche");
        verify(subcategoryProductRepo).findByNameAndCategoryId("Leche", 1L);
        verify(subcategoryProductRepo).save(subcategory);
        assertEquals("Leche", subcategory.getName());
    }
    @Test
    public void save_invalidExistingSubcategory(){
        subcategory.setName("Arroz");
        doNothing().when(validator).validateSubcategory("Arroz");
        when(subcategoryProductRepo.findByNameAndCategoryId("Arroz", 1L)).thenReturn(Optional.of(existingSubcategory));
        when(helperService.capitalize("Arroz")).thenReturn("Arroz");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subcategoryService.save(subcategory);
        });

        assertEquals("Nombre Existente: Hay registro de esta Subcategoría en la Categoría " + category.getName() + " .", exception.getMessage());
        verify(validator).validateSubcategory("Arroz");
        verify(subcategoryProductRepo).findByNameAndCategoryId("Arroz", 1L);
        verify(subcategoryProductRepo, never()).save(subcategory);
    }
    @Test
    public void save_invalidNullCategory(){
        subcategory.setCategory(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subcategoryService.save(subcategory);
        });

        assertEquals("Dede seleccionar una categoría.", exception.getMessage());
    }

    //Prueba para obtener todas las subcategorías
    @Test
    public void getAll_valid(){
        subcategoryService.getAll();
        verify(subcategoryProductRepo).findAll();
    }

    //Prueba para obtener una lista de subcategoría por ID de categoría
    @Test
    public void getSubcategoryByCategoryID_valid(){
        when(categoryProductRepo.getReferenceById(1L)).thenReturn(category);
        List<SubcategoryProduct> expectedSubcategories = new ArrayList<>();
        expectedSubcategories.add(existingSubcategory);
        expectedSubcategories.add(existingSubcategory2);

        when(subcategoryProductRepo.findByCategoryId(category.getId())).thenReturn(expectedSubcategories);
        List<SubcategoryProduct> actualSubcategories = subcategoryService.getSubcategoryByCategoryID(1L);

        assertEquals(expectedSubcategories.size(), actualSubcategories.size());
        assertEquals(expectedSubcategories.get(0).getName(), actualSubcategories.get(0).getName());
        assertEquals(expectedSubcategories.get(1).getName(), actualSubcategories.get(1).getName());

        verify(categoryProductRepo).getReferenceById(1L);
        verify(subcategoryProductRepo).findByCategoryId(category.getId());
    }
}
