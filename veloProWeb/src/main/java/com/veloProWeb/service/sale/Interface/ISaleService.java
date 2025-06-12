package com.veloProWeb.service.sale.Interface;

import com.veloProWeb.model.dto.sale.SaleRequestDTO;
import com.veloProWeb.model.dto.sale.SaleResponseDTO;
import com.veloProWeb.model.entity.Sale.Sale;

import java.util.List;

public interface ISaleService {
    Sale createSale(SaleRequestDTO dto);
    Long totalSales();
    List<SaleResponseDTO> getAllSales();
    Sale getSaleById(Long id);
}
