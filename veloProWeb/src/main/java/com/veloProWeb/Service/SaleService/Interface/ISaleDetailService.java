package com.veloProWeb.Service.SaleService.Interface;

import com.veloProWeb.Model.DTO.DetailSaleDTO;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Entity.Sale.SaleDetail;

import java.util.List;

public interface ISaleDetailService {
    void createSaleDetails(List<DetailSaleDTO> dto, Sale sale);
    List<SaleDetail> getAll();
}
