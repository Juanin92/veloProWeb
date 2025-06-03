package com.veloProWeb.service.inventory;

import com.veloProWeb.model.dto.inventory.KardexResponseDTO;
import com.veloProWeb.model.entity.inventory.Kardex;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.model.Enum.MovementsType;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;

public interface IKardexService {
    void addKardex(UserDetails userDetails, Product product, int quantity, String comment, MovementsType moves);
    List<KardexResponseDTO> getAll();
    List<Kardex> getProductMovementsSinceDate(Product product, LocalDate starDate);
}
