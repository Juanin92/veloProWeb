package com.veloProWeb.Service.Report;

import com.veloProWeb.Model.DTO.KardexRequestDTO;
import com.veloProWeb.Model.Entity.Kardex;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Enum.MovementsType;
import com.veloProWeb.Repository.KardexRepo;
import com.veloProWeb.Service.Product.Interfaces.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class KardexService implements IkardexService{
    @Autowired private KardexRepo kardexRepo;
    @Autowired private IProductService productService;

    /**
     * Crea un registro/movimiento de productos (Kardex)
     * @param product - producto modificado
     * @param quantity - cantidad modificada
     * @param comment - comentario del registro
     * @param moves - tipo de movimiento realizado
     */
    @Override
    public void addKardex(Product product, int quantity, String comment, MovementsType moves) {
        Kardex kardex = new Kardex();
        kardex.setDate(LocalDate.now());
        kardex.setQuantity(quantity);
        kardex.setMovementsType(moves);
        kardex.setComment(comment);
        kardex.setProduct(product);
        kardex.setStock(product.getStock());
        kardex.setPrice(product.getBuyPrice());
        kardex.setUser(null);
        kardexRepo.save(kardex);
    }

    /**
     * Obtiene una lista de registro de movimientos de productos (Kardex)
     * @return - Lista con los registros
     */
    @Override
    public List<Kardex> getAll() {
        return kardexRepo.findAll();
    }
}
