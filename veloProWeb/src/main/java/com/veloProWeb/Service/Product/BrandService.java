package com.veloProWeb.Service.Product;

import com.veloProWeb.Model.Entity.Product.BrandProduct;
import com.veloProWeb.Repository.Product.BrandProductRepo;
import com.veloProWeb.Service.Product.Interfaces.IBrandService;
import com.veloProWeb.Validation.CategoriesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandService implements IBrandService {

    @Autowired private BrandProductRepo brandProductRepo;
    @Autowired private CategoriesValidator validator;

    @Override
    public void save(BrandProduct brand) {
        validator.validateBrand(brand.getName());
        BrandProduct brandProduct = getBrandCreated(capitalize(brand.getName()));
        if (brandProduct != null){
            throw new IllegalArgumentException("Nombre Existente: Hay registro de este marca.");
        } else {

            brand.setName(capitalize(brand.getName()));
            brandProductRepo.save(brand);
        }
    }

    @Override
    public List<BrandProduct> getAll() {
        return brandProductRepo.findAll();
    }

    private BrandProduct getBrandCreated(String name){
        Optional<BrandProduct> optionalBrandProduct = brandProductRepo.findByName(name);
        return optionalBrandProduct.orElse(null);
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
