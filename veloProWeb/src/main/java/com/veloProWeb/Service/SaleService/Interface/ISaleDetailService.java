package com.veloProWeb.Service.SaleService.Interface;

import com.veloProWeb.Model.DTO.DetailSaleDTO;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Entity.Sale.SaleDetail;

import java.util.List;

public interface ISaleDetailService {
    void createSaleDetails(DetailSaleDTO dto, Sale sale, Product product);
    List<SaleDetail> getAll();
    DetailSaleDTO createDTO(Product product);
    int deleteProduct(List<DetailSaleDTO> dtoList, Long id, int total);
    List<DetailSaleDTO> findDetailSaleBySaleId(Long id);
}
