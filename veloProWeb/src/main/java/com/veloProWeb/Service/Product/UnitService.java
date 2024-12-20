package com.veloProWeb.Service.Product;

import com.veloProWeb.Model.Entity.Product.UnitProduct;
import com.veloProWeb.Repository.Product.UnitProductRepo;
import com.veloProWeb.Service.Product.Interfaces.IUnitService;
import com.veloProWeb.Validation.CategoriesValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UnitService implements IUnitService {

    @Autowired private UnitProductRepo unitProductRepo;
    @Autowired private CategoriesValidator validator;

    @Override
    public void save(UnitProduct unit) {
        UnitProduct unitProduct = getUnitCreated(capitalize(unit.getNameUnit()));
        if (unitProduct != null){
            throw new IllegalArgumentException("Nombre Existente: Hay registro de esta unidad de medida.");
        } else {
            validator.validateUnit(unit.getNameUnit());
            int digitCount = unit.getNameUnit().replaceAll("[^0-9]", "").length();
            int letterCount = unit.getNameUnit().replaceAll("[^a-zA-Z]", "").length();
            if (digitCount <= 2 && letterCount <= 2) {
                unit.setNameUnit(capitalize(unit.getNameUnit()));
                unitProductRepo.save(unit);
            } else {
                throw new IllegalArgumentException("El nombre debe tener máximo 2 dígitos y 2 letras.");
            }
        }
    }

    @Override
    public List<UnitProduct> getAll() {
        return unitProductRepo.findAll();
    }

    private UnitProduct getUnitCreated(String name){
        Optional<UnitProduct> unitProduct = unitProductRepo.findByNameUnit(name);
        return unitProduct.orElse(null);
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String[] words = value.split(" ");
        StringBuilder capitalized = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(word.toUpperCase()).append(" ");
            }
        }
        return capitalized.toString().trim();
    }
}
