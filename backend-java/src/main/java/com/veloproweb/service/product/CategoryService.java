package com.veloproweb.service.product;

import com.veloproweb.model.entity.product.CategoryProduct;
import com.veloproweb.repository.product.CategoryProductRepo;
import com.veloproweb.service.product.interfaces.ICategoryService;
import com.veloproweb.util.TextFormatter;
import com.veloproweb.validation.CategoryValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryService implements ICategoryService {

   private final CategoryProductRepo categoryProductRepo;
   private final CategoryValidator validator;

    /**
     * Método para crear un objeto de categoría (nombre)
     * Válida el parametro de nombre
     * Se busca si existe ya un registro del objeto, si no lanza una excepción
     * @param category - categoría con los detalles
     */
    @Override
    public void save(CategoryProduct category) {
        String capitalizedName = TextFormatter.capitalize(category.getName());
        CategoryProduct categoryProduct = getCategoryCreated(capitalizedName);
        validator.validateCategoryDoesNotExist(categoryProduct);
        category.setId(null);
        category.setName(capitalizedName);
        categoryProductRepo.save(category);
    }

    /**
     * Obtiene una lista de categorías registradas
     * @return - devuelve una lista de categorías ordenadas alfabéticamente
     */
    @Override
    public List<CategoryProduct> getAll() {
        return categoryProductRepo.findAllOrderByNameAsc();
    }

    /**
     * Busca un registro de una categoría existente
     * @param name - Nombre de la categoría a buscar
     * @return - Devuelve el objeto encontrado o vacío
     */
    private CategoryProduct getCategoryCreated(String name){
        Optional<CategoryProduct> categoryProduct = categoryProductRepo.findByName(name);
        return categoryProduct.orElse(null);
    }
}
