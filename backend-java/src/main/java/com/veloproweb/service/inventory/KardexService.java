package com.veloproweb.service.inventory;

import com.veloproweb.mapper.KardexMapper;
import com.veloproweb.model.dto.inventory.KardexResponseDTO;
import com.veloproweb.model.entity.inventory.Kardex;
import com.veloproweb.model.entity.user.User;
import com.veloproweb.model.entity.product.Product;
import com.veloproweb.model.Enum.MovementsType;
import com.veloproweb.repository.KardexRepo;
import com.veloproweb.service.user.interfaces.IUserService;
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
        User user = userService.getUserByUsername(userDetails.getUsername());
        Kardex kardex = mapper.toEntity(product, quantity, comment, moves, user);
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
     * Obtiene los registros de movimiento de un producto dependiendo de una fecha
     * @param product - Producto a buscar movimientos
     * @param startDate - Fecha desde que se debe buscar el registro
     * @return - una lista con los registros encontrados
     */
    @Override
    public List<Kardex> getProductMovementsSinceDate(Product product, LocalDate startDate) {
        return kardexRepo.findByProductAndDateAfter(product, startDate);
    }
}
