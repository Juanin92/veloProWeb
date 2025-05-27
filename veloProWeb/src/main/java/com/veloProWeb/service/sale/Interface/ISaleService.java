package com.veloProWeb.service.sale.Interface;

import com.veloProWeb.model.dto.SaleRequestDTO;
import com.veloProWeb.model.entity.Sale.Sale;

import java.util.List;
import java.util.Optional;

public interface ISaleService {
    Sale createSale(SaleRequestDTO dto);
    Long totalSales();
    List<SaleRequestDTO> getAllSale();
    Optional<Sale> getSaleById(Long id);
}
