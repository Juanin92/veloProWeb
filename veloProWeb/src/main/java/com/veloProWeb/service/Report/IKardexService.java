package com.veloProWeb.service.Report;

import com.veloProWeb.model.dto.KardexResponseDTO;
import com.veloProWeb.model.entity.product.Product;
import com.veloProWeb.model.Enum.MovementsType;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IKardexService {
    void addKardex(UserDetails userDetails, Product product, int quantity, String comment, MovementsType moves);
    List<KardexResponseDTO> getAll();
    void checkLowSales(Product product);
}
