package com.veloproweb.service.sale.interfaces;

import com.veloproweb.model.dto.sale.SaleDetailRequestDTO;
import com.veloproweb.model.dto.sale.SaleDetailResponseDTO;
import com.veloproweb.model.entity.sale.Dispatch;
import com.veloproweb.model.entity.sale.Sale;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface ISaleDetailService {
    void addDetailsToSale(List<SaleDetailRequestDTO> dto, Sale sale, UserDetails userDetails);
    List<SaleDetailResponseDTO> getDetailsBySaleId(Long idSale);
    void createSaleDetailsToDispatch(List<SaleDetailRequestDTO> dto, Dispatch dispatch);
    void addSaleToSaleDetailsDispatch(Long idDispatch, Sale sale);
}
