package com.veloProWeb.service.Report;

import com.veloProWeb.mapper.KardexMapper;
import com.veloProWeb.model.dto.KardexResponseDTO;
import com.veloProWeb.model.entity.Kardex;
import com.veloProWeb.model.entity.User.User;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.model.Enum.MovementsType;
import com.veloProWeb.repository.KardexRepo;
import com.veloProWeb.service.User.Interface.IAlertService;
import com.veloProWeb.service.User.Interface.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class KardexService implements IKardexService {

    private final KardexRepo kardexRepo;
    private final IAlertService alertService;
    private final IUserService userService;
    private final KardexMapper mapper;

    /**
     * Crea un registro/movimiento de productos (Kardex)
     * @param product - producto modificado
     * @param quantity - cantidad modificada
     * @param comment - comentario del registro
     * @param moves - tipo de movimiento realizado
     */
    @Transactional
    @Override
    public void addKardex(UserDetails userDetails, Product product, int quantity, String comment, MovementsType moves) {
        User user = userService.getUserWithUsername(userDetails.getUsername());
        Kardex kardex = Kardex.builder()
                .date(LocalDate.now())
                .quantity(quantity)
                .movementsType(moves)
                .comment(comment)
                .product(product)
                .price(product.getBuyPrice())
                .stock(product.getStock())
                .user(user)
                .build();
        kardexRepo.save(kardex);
    }

    /**
     * Obtiene una lista de registro de movimientos de productos (Kardex)
     *
     * @return - Lista con los registros
     */
    @Override
    public List<KardexResponseDTO> getAll() {
        return kardexRepo.findAll().stream()
                .map(mapper::toResponseDTO)
                .toList();
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
        String description = String.format("Producto sin Ventas (+ 90 días) -> %s", product.getDescription());
        if (difference > (totalEntries / 2) && !alertService.isAlertActive(product, description)){
            alertService.createAlert(product, description);
        }
    }
}
