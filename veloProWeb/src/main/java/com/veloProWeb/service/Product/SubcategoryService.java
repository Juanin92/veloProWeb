package com.veloProWeb.service.Product;

import com.veloProWeb.model.entity.Product.SubcategoryProduct;
import com.veloProWeb.repository.Product.CategoryProductRepo;
import com.veloProWeb.repository.Product.SubcategoryProductRepo;
import com.veloProWeb.service.Product.Interfaces.ISubcategoryService;
import com.veloProWeb.util.TextFormatter;
import com.veloProWeb.validation.SubcategoryValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SubcategoryService implements ISubcategoryService {

    private final SubcategoryProductRepo subcategoryProductRepo;
    private final CategoryProductRepo categoryProductRepo;
    private final SubcategoryValidator validator;

    /**
     * Crea un objeto de subcategoría
     * Válida que tenga una categoría seleccionada y
     * se busca si existe ya un registro del objeto, si no lanza una excepción
     * @param subcategory - Subcategoría con los detalles
     */
    @Transactional
    @Override
    public void save(SubcategoryProduct subcategory) {
        String capitalizedName = TextFormatter.capitalize(subcategory.getName());
        validator.validateSubcategoryHasCategory(subcategory);
        SubcategoryProduct subcategoryProduct = getSubcategoryCreated(capitalizedName,
                subcategory.getCategory().getId());
        validator.validateSubcategoryDoesNotExist(subcategoryProduct);
        subcategory.setId(null);
        subcategory.setName(capitalizedName);
        subcategoryProductRepo.save(subcategory);
    }

    /**
     * Obtiene una lista de subcategorías asociadas una categoría por su ID.
     * @param id - Identificador de la categoría
     * @return - lista de subcategorías asociada a la categoría ordenadas alfabéticamente
     */
    @Override
    public List<SubcategoryProduct> getSubcategoryByCategoryID(Long id) {
        return categoryProductRepo.findById(id)
                .map(category -> subcategoryProductRepo.findByCategoryId(category.getId()).stream()
                        .sorted(Comparator.comparing(SubcategoryProduct::getName))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
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
