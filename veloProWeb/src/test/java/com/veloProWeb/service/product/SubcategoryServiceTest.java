package com.veloProWeb.service.product;

import com.veloProWeb.exceptions.product.CategoryNotFoundException;
import com.veloProWeb.exceptions.product.SubcategoryAlreadyExistsException;
import com.veloProWeb.model.entity.product.CategoryProduct;
import com.veloProWeb.model.entity.product.SubcategoryProduct;
import com.veloProWeb.repository.product.CategoryProductRepo;
import com.veloProWeb.repository.product.SubcategoryProductRepo;
import com.veloProWeb.validation.SubcategoryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubcategoryServiceTest {

    @InjectMocks private SubcategoryService subcategoryService;
    @Mock private SubcategoryProductRepo subcategoryProductRepo;
    @Mock private CategoryProductRepo categoryProductRepo;
    @Mock private SubcategoryValidator validator;
    private SubcategoryProduct subcategory;
    private CategoryProduct category;

    @BeforeEach
    void setUp(){
        category = CategoryProduct.builder().id(1L).name("Food").build();
    }

    //Prueba para crear una nueva subcategoría
    @Test
    public void save_valid(){
        subcategory = SubcategoryProduct.builder().id(null).name("Milk").category(category).build();
        when(subcategoryProductRepo.findByNameAndCategoryId(subcategory.getName(), category.getId()))
                .thenReturn(Optional.empty());
        doNothing().when(validator).validateSubcategoryDoesNotExist(null);
        doNothing().when(validator).validateSubcategoryHasCategory(subcategory);

        subcategoryService.save(subcategory);

        ArgumentCaptor<SubcategoryProduct> subcategoryArgument = ArgumentCaptor.forClass(SubcategoryProduct.class);
        verify(subcategoryProductRepo, times(1)).findByNameAndCategoryId(subcategory.getName(),
                category.getId());
        verify(subcategoryProductRepo, times(1)).save(subcategoryArgument.capture());

        SubcategoryProduct result = subcategoryArgument.getValue();
        assertNull(result.getId());
        assertEquals("Milk", result.getName());
        assertEquals(category, result.getCategory());
    }
    @Test
    public void save_invalidExistingSubcategory(){
        subcategory = SubcategoryProduct.builder().id(null).name("Milk").category(category).build();
        when(subcategoryProductRepo.findByNameAndCategoryId(subcategory.getName(), category.getId()))
                .thenReturn(Optional.of(subcategory));
        doThrow(new SubcategoryAlreadyExistsException(String.format("Nombre Existente: Hay registro de esta " +
                        "Subcategoría en la Categoría %s", subcategory.getCategory().getName())))
                .when(validator).validateSubcategoryDoesNotExist(subcategory);

        SubcategoryAlreadyExistsException exception = assertThrows(SubcategoryAlreadyExistsException.class,
                () -> subcategoryService.save(subcategory));

        verify(subcategoryProductRepo, times(1))
                .findByNameAndCategoryId(subcategory.getName(), category.getId());
        verify(validator, times(1)).validateSubcategoryDoesNotExist(subcategory);
        verify(subcategoryProductRepo, never()).save(subcategory);

        assertEquals(String.format("Nombre Existente: Hay registro de esta Subcategoría en la Categoría %s",
                subcategory.getCategory().getName()), exception.getMessage());
    }
    @Test
    public void save_invalidNullCategory(){
        subcategory = SubcategoryProduct.builder().id(null).name("Milk").category(null).build();
        doThrow(new CategoryNotFoundException("Dede seleccionar una categoría."))
                .when(validator).validateSubcategoryHasCategory(subcategory);

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class,
                () -> validator.validateSubcategoryHasCategory(subcategory));

        verify(subcategoryProductRepo, never()).findByNameAndCategoryId(subcategory.getName(), null);
        verify(validator, times(1)).validateSubcategoryHasCategory(subcategory);
        verify(subcategoryProductRepo, never()).save(subcategory);

        assertEquals("Dede seleccionar una categoría.", exception.getMessage());
    }

    //Prueba para obtener una lista de subcategoría por ID de categoría
    @Test
    public void getSubcategoryByCategoryID_valid(){
        subcategory = SubcategoryProduct.builder().id(1L).name("Milk").category(category).build();
        SubcategoryProduct subcategory2 = SubcategoryProduct.builder().id(2L).name("Pasta").category(category).build();
        SubcategoryProduct subcategory3 = SubcategoryProduct.builder().id(3L).name("Meat").category(category).build();

        when(categoryProductRepo.findById(1L)).thenReturn(Optional.of(category));

        List<SubcategoryProduct> expectedSubcategories = List.of(subcategory, subcategory2, subcategory3);
        when(subcategoryProductRepo.findByCategoryId(category.getId())).thenReturn(expectedSubcategories);

        List<SubcategoryProduct> result = subcategoryService.getSubcategoryByCategoryID(1L);

        assertEquals(expectedSubcategories.size(), result.size());
        assertEquals(expectedSubcategories.get(0).getName(), result.get(0).getName());
        assertEquals(expectedSubcategories.get(1).getName(), result.get(1).getName());

        verify(categoryProductRepo, times(1)).findById(1L);
        verify(subcategoryProductRepo, times(1)).findByCategoryId(category.getId());
    }
    @Test
    public void getSubcategoryByCategoryID_validEmptyList(){
        when(categoryProductRepo.findById(1L)).thenReturn(Optional.of(category));

        List<SubcategoryProduct> expectedSubcategories = List.of();
        when(subcategoryProductRepo.findByCategoryId(category.getId())).thenReturn(expectedSubcategories);

        List<SubcategoryProduct> result = subcategoryService.getSubcategoryByCategoryID(1L);

        assertEquals(0, result.size());

        verify(categoryProductRepo, times(1)).findById(1L);
        verify(subcategoryProductRepo, times(1)).findByCategoryId(category.getId());
    }
}
