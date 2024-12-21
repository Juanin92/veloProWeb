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

    /**
     * Método para crear un objeto de unidad de medida (nombre)
     * Válida que el objeto no sea nulo y el parametro de nombre
     * Se busca si existe ya un registro del objeto, si no lanza una excepción
     * Separa el nombre por números y letra y válida que sean menor a 2 caracteres
     * @param unit - Unidad de medida con los detalles
     */
    @Override
    public void save(UnitProduct unit) {
        validator.validateUnit(unit.getNameUnit());
        UnitProduct unitProduct = getUnitCreated(capitalize(unit.getNameUnit()));
        if (unitProduct != null){
            throw new IllegalArgumentException("Nombre Existente: Hay registro de esta unidad de medida.");
        } else {
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

    /**
     * Obtiene una lista de unidades de medidas registradas
     * @return - devuelve una lista de unidades de medidas
     */
    @Override
    public List<UnitProduct> getAll() {
        return unitProductRepo.findAll();
    }

    /**
     * Busca un registro de una unidad de medida existente
     * @param name - Nombre de la unidad de medida a buscar
     * @return - Devuelve el objeto encontrado o vacío
     */
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
