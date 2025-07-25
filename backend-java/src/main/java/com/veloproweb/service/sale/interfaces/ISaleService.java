package com.veloproweb.service.sale.interfaces;

import com.veloproweb.model.dto.sale.SaleRequestDTO;
import com.veloproweb.model.dto.sale.SaleResponseDTO;
import com.veloproweb.model.entity.sale.Sale;

import java.util.List;

public interface ISaleService {
    Sale createSale(SaleRequestDTO dto);
    Long totalSales();
    List<SaleResponseDTO> getAllSales();
    Sale getSaleById(Long id);
}
