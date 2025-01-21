package com.veloProWeb.Service.Sale.Interface;

import com.veloProWeb.Model.DTO.SaleRequestDTO;
import com.veloProWeb.Model.Entity.Sale.Sale;

public interface ISaleService {
    Sale createSale(SaleRequestDTO dto);
    Long totalSales();
}
