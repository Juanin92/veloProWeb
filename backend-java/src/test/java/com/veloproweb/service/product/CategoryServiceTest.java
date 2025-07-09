package com.veloproweb.service.product;

import com.veloproweb.exceptions.product.CategoryAlreadyExistsException;
import com.veloproweb.model.entity.product.CategoryProduct;
import com.veloproweb.repository.product.CategoryProductRepo;
import com.veloproweb.validation.CategoryValidator;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks private CategoryService categoryService;
    @Mock private CategoryProductRepo categoryProductRepo;
    @Mock private CategoryValidator validator;
    private CategoryProduct category, category2, category3, existingCategory;

    @BeforeEach
    void setUp(){
        category = CategoryProduct.builder().id(1L).name("Tech").build();
        category2 = CategoryProduct.builder().id(2L).name("Food").build();
        category3 = CategoryProduct.builder().id(3L).name("Clothes").build();
        existingCategory = CategoryProduct.builder().id(5L).name("Cleaning").build();
    }

    //Prueba para crear una nueva marca
    @Test
    void save_valid(){
        when(categoryProductRepo.findByName("Tech")).thenReturn(Optional.empty());
        doNothing().when(validator).validateCategoryDoesNotExist(null);

        categoryService.save(category);

        verify(categoryProductRepo, times(1)).findByName("Tech");
        verify(categoryProductRepo, times(1)).save(category);
        assertEquals("Tech", category.getName());
    }
    @Test
    void save_invalidExistingCategory(){
        when(categoryProductRepo.findByName("Cleaning")).thenReturn(Optional.of(existingCategory));
        doThrow(new CategoryAlreadyExistsException("Nombre Existente: Hay registro de esta categoría.")).when(validator)
                .validateCategoryDoesNotExist(existingCategory);

        CategoryAlreadyExistsException exception = assertThrows(CategoryAlreadyExistsException.class,
                () -> categoryService.save(existingCategory));

        verify(categoryProductRepo, times(1)).findByName("Cleaning");
        verify(categoryProductRepo, never()).save(existingCategory);

        assertEquals("Nombre Existente: Hay registro de esta categoría.", exception.getMessage());
    }

    //Prueba para obtener todas las marcas
    @Test
    void getAll_valid(){
        List<CategoryProduct> categories = List.of(existingCategory, category3, category2, category);
        when(categoryProductRepo.findAllOrderByNameAsc()).thenReturn(categories);

        List<CategoryProduct> result = categoryService.getAll();

        verify(categoryProductRepo, times(1)).findAllOrderByNameAsc();

        assertEquals(result.size(), categories.size());
        assertEquals(List.of(existingCategory, category3, category2, category), result);
    }

}
