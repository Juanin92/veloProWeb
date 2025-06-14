package com.veloproweb.service.inventory;

import com.veloproweb.model.dto.inventory.KardexResponseDTO;
import com.veloproweb.model.entity.inventory.Kardex;
import com.veloproweb.model.entity.product.Product;
import com.veloproweb.model.Enum.MovementsType;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;

public interface IKardexService {
    void addKardex(UserDetails userDetails, Product product, int quantity, String comment, MovementsType moves);
    List<KardexResponseDTO> getAll();
    List<Kardex> getProductMovementsSinceDate(Product product, LocalDate starDate);
}
