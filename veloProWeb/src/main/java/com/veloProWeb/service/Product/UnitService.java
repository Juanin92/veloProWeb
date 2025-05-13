package com.veloProWeb.service.Product;

import com.veloProWeb.model.entity.Product.UnitProduct;
import com.veloProWeb.repository.Product.UnitProductRepo;
import com.veloProWeb.service.Product.Interfaces.IUnitService;
import com.veloProWeb.util.TextFormatter;
import com.veloProWeb.validation.UnitValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UnitService implements IUnitService {

    private final UnitProductRepo unitProductRepo;
    private final UnitValidator validator;

    /**
     * Método para crear un objeto de unidad de medida
     * Válida que el objeto
     * @param unit - Unidad de medida con los detalles
     */
    @Override
    public void save(UnitProduct unit) {
        String capitalizedName = TextFormatter.upperCaseWord((unit.getNameUnit()));
        UnitProduct unitProduct = getUnitCreated(capitalizedName);
        validator.validateUnitDoesNotExist(unitProduct);
        unit.setId(null);
        unit.setNameUnit(capitalizedName);
        validator.validateUnitNameFormat(unit);
        unitProductRepo.save(unit);
    }

    /**
     * Obtiene una lista de unidades de medidas registradas
     * @return - devuelve una lista de unidades de medidas ordenadas alfabéticamente
     */
    @Override
    public List<UnitProduct> getAll() {
        return unitProductRepo.findAllOrderByNameAsc();
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
}
