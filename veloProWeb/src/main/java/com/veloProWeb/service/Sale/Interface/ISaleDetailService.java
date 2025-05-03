package com.veloProWeb.service.Sale.Interface;

import com.veloProWeb.model.dto.DetailSaleDTO;
import com.veloProWeb.model.dto.DetailSaleRequestDTO;
import com.veloProWeb.model.entity.Sale.Dispatch;
import com.veloProWeb.model.entity.Sale.Sale;
import com.veloProWeb.model.entity.Sale.SaleDetail;

import java.util.List;

public interface ISaleDetailService {
    void createSaleDetailsToSale(List<DetailSaleDTO> dto, Sale sale);
    List<SaleDetail> getAll();
    List<DetailSaleRequestDTO> getSaleDetailsToSale(Long idSale);
    void createSaleDetailsToDispatch(List<DetailSaleDTO> dto, Dispatch dispatch);
    List<DetailSaleRequestDTO> getSaleDetailsToDispatch(Long idDispatch);
    void addSaleToSaleDetailsDispatch(Long idDispatch, Sale sale);
}
