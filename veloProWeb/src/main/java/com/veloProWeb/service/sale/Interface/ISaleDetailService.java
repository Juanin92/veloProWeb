package com.veloProWeb.service.sale.Interface;

import com.veloProWeb.model.dto.sale.SaleDetailRequestDTO;
import com.veloProWeb.model.dto.sale.SaleDetailResponseDTO;
import com.veloProWeb.model.entity.Sale.Dispatch;
import com.veloProWeb.model.entity.Sale.Sale;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface ISaleDetailService {
    void addDetailsToSale(List<SaleDetailRequestDTO> dto, Sale sale, UserDetails userDetails);
    List<SaleDetailResponseDTO> getDetailsBySaleId(Long idSale);
    void createSaleDetailsToDispatch(List<SaleDetailRequestDTO> dto, Dispatch dispatch);
    void addSaleToSaleDetailsDispatch(Long idDispatch, Sale sale);
}
