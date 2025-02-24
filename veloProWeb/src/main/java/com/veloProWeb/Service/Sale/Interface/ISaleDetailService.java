package com.veloProWeb.Service.Sale.Interface;

import com.veloProWeb.Model.DTO.DetailSaleDTO;
import com.veloProWeb.Model.DTO.DetailSaleRequestDTO;
import com.veloProWeb.Model.Entity.Sale.Dispatch;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Entity.Sale.SaleDetail;

import java.util.List;

public interface ISaleDetailService {
    void createSaleDetailsToSale(List<DetailSaleDTO> dto, Sale sale);
    List<SaleDetail> getAll();
    List<DetailSaleRequestDTO> getSaleDetailsToSale(Long idSale);
    void createSaleDetailsToDispatch(List<DetailSaleDTO> dto, Dispatch dispatch);
    List<DetailSaleRequestDTO> getSaleDetailsToDispatch(Long idDispatch);
    void addSaleToSaleDetailsDispatch(Long idDispatch, Sale sale);
}
