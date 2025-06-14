package com.veloproweb.service.product;

import com.veloproweb.model.entity.product.BrandProduct;
import com.veloproweb.repository.product.BrandProductRepo;
import com.veloproweb.service.product.interfaces.IBrandService;
import com.veloproweb.util.TextFormatter;
import com.veloproweb.validation.BrandValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BrandService implements IBrandService {

    private final BrandProductRepo brandProductRepo;
    private final BrandValidator validator;

    /**
     * Método para crear un objeto de marca
     * Válida la existencia de la marca
     * @param brand - Marca con los detalles
     */
    @Transactional
    @Override
    public void save(BrandProduct brand) {
        String capitalizedName = TextFormatter.capitalize(brand.getName());
        BrandProduct brandProduct = getBrandCreated(capitalizedName);
        validator.validateBrandDoesNotExist(brandProduct);
        brand.setId(null);
        brand.setName(capitalizedName);
        brandProductRepo.save(brand);
    }

    /**
     * Obtiene una lista de marcas registradas
     * @return - devuelve una lista de marcas ordenadas alfabéticamente
     */
    @Override
    public List<BrandProduct> getAll() {
        return brandProductRepo.findAllOrderByNameAsc();
    }

    /**
     * Busca un registro de marca existente
     * @param name - Nombre de la marca a buscar
     * @return - Devuelve el objeto encontrado o vacío
     */
    private BrandProduct getBrandCreated(String name){
        Optional<BrandProduct> optionalBrandProduct = brandProductRepo.findByName(name);
        return optionalBrandProduct.orElse(null);
    }
}
