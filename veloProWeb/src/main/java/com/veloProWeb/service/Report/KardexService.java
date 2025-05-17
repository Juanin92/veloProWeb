package com.veloProWeb.service.Report;

import com.veloProWeb.model.entity.Kardex;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.repository.KardexRepo;
import com.veloProWeb.service.User.Interface.IAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class KardexService implements IkardexService{
    @Autowired private KardexRepo kardexRepo;
    @Autowired private IAlertService alertService;

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

    /**
     * Verifica las ventas bajas de un producto en un período de 90 días y crea una alerta.
     * Recupera la lista de movimientos de Kardex para el producto desde la fecha calculada.
     * Compara las entradas y salidas para determinar si hay una diferencia que indique bajas ventas.
     * Crea una alerta si la diferencia es mayor a la mitad de las entradas y no existe una alerta activa para el producto.
     * @param product - Producto a verificar sus ventas
     */
    @Override
    public void checkLowSales(Product product) {
        LocalDate days = LocalDate.now().minusDays(90);
        List<Kardex> kardexList = kardexRepo.findByProductAndDateAfter(product, days);

        int totalEntries = 0;
        int totalExits = 0;
        for (Kardex kardex : kardexList){
            if (kardex.getMovementsType().equals(MovementsType.ENTRADA)){
                totalEntries = kardex.getQuantity();
            } else if (kardex.getMovementsType().equals(MovementsType.SALIDA)) {
                totalExits = kardex.getQuantity();
            }
        }
        int difference = totalEntries - totalExits;
        String description = "Producto sin Ventas (+ 90 días), " + product.getDescription();
        if (difference > (totalEntries / 2) && !alertService.isAlertActive(product, description)){
            alertService.createAlert(product, description);
        }
    }
}
