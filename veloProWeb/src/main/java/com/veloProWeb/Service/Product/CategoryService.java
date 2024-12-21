package com.veloProWeb.Service.Product;

import com.veloProWeb.Model.Entity.Product.CategoryProduct;
import com.veloProWeb.Repository.Product.CategoryProductRepo;
import com.veloProWeb.Service.Product.Interfaces.ICategoryService;
import com.veloProWeb.Validation.CategoriesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {

    @Autowired private CategoryProductRepo categoryProductRepo;
    @Autowired private CategoriesValidator validator;

    /**
     * Método para crear un objeto de categoría (nombre)
     * Válida el parametro de nombre
     * Se busca si existe ya un registro del objeto, si no lanza una excepción
     * @param category - categoría con los detalles
     */
    @Override
    public void save(CategoryProduct category) {
        validator.validateCategory(category.getName());
        CategoryProduct categoryProduct = getCategoryCreated(capitalize(category.getName()));
        if (categoryProduct != null){
            throw new IllegalArgumentException("Nombre Existente: Hay registro de esta categoría.");
        } else {
            category.setName(capitalize(category.getName()));
            categoryProductRepo.save(category);
        }
    }

    /**
     * Obtiene una lista de categorías registradas
     * @return - devuelve una lista de categorías
     */
    @Override
    public List<CategoryProduct> getAll() {
        return categoryProductRepo.findAll();
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

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String[] words = value.split(" ");
        StringBuilder capitalized = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(word.substring(0, 1).toUpperCase());
                capitalized.append(word.substring(1).toLowerCase());
                capitalized.append(" ");
            }
        }
        return capitalized.toString().trim();
    }
}
