package com.veloProWeb.Service.Product;

import com.veloProWeb.Model.Entity.Product.CategoryProduct;
import com.veloProWeb.Model.Entity.Product.SubcategoryProduct;
import com.veloProWeb.Repository.Product.CategoryProductRepo;
import com.veloProWeb.Repository.Product.SubcategoryProductRepo;
import com.veloProWeb.Service.Product.Interfaces.ISubcategoryService;
import com.veloProWeb.Utils.HelperService;
import com.veloProWeb.Validation.CategoriesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubcategoryService implements ISubcategoryService {

    @Autowired private SubcategoryProductRepo subcategoryProductRepo;
    @Autowired private CategoryProductRepo categoryProductRepo;
    @Autowired private CategoriesValidator validator;
    @Autowired private HelperService helperService;

    /**
     * Método para crear un objeto de subcategoría (nombre)
     * Válida que tenga una categoría seleccionada y el parametro de nombre
     * Se busca si existe ya un registro del objeto, si no lanza una excepción
//     * @param category - Categoría con los detalles
     * @param subcategory - Subcategoría con los detalles
     */
    @Override
    public void save(SubcategoryProduct subcategory) {
        if (subcategory.getCategory() != null){
            validator.validateSubcategory(subcategory.getName());
            SubcategoryProduct subcategoryProduct = getSubcategoryCreated(helperService.capitalize(subcategory.getName()), subcategory.getCategory().getId());
            if (subcategoryProduct != null){
                throw new IllegalArgumentException("Nombre Existente: Hay registro de esta Subcategoría en la Categoría " + subcategory.getCategory().getName() + " .");
            } else {
                subcategory.setId(null);
                subcategory.setName(helperService.capitalize(subcategory.getName()));
                subcategoryProductRepo.save(subcategory);
            }
        }else {
            throw new IllegalArgumentException("Dede seleccionar una categoría.");
        }
    }

    /**
     * Obtiene una lista de subcategorías registradas
     * @return - devuelve una lista de subcategorías
     */
    @Override
    public List<SubcategoryProduct> getAll() {
        return subcategoryProductRepo.findAll();
    }

    @Override
    public List<SubcategoryProduct> getSubcategoryByCategoryID(Long id) {
        CategoryProduct category = categoryProductRepo.getReferenceById(id);
        return subcategoryProductRepo.findByCategoryId(category.getId());
    }

    /**
     * Busca un registro de una subcategoría existente
     * @param name - Nombre de la subcategoría a buscar
     * @param id - Identificador de la categoría asociada a la subcategoría
     * @return - Devuelve el objeto encontrado o vacío
     */
    private SubcategoryProduct getSubcategoryCreated(String name, Long id){
        Optional<SubcategoryProduct> subcategoryProduct = subcategoryProductRepo.findByNameAndCategoryId(name, id);
        return subcategoryProduct.orElse(null);
    }
}
