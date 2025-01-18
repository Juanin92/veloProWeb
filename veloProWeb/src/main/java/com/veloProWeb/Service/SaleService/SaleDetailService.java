package com.veloProWeb.Service.SaleService;

import com.veloProWeb.Model.DTO.DetailSaleDTO;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Entity.Sale.SaleDetail;
import com.veloProWeb.Repository.Sale.SaleDetailRepo;
import com.veloProWeb.Service.SaleService.Interface.ISaleDetailService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SaleDetailService implements ISaleDetailService {

    @Autowired private SaleDetailRepo saleDetailRepo;

    @Override
    public void createSaleDetails(DetailSaleDTO dto, Sale sale, Product product) {
        SaleDetail saleDetail = new SaleDetail();
        saleDetail.setTotal(dto.getTotal());
        saleDetail.setTax(dto.getTax());
        saleDetail.setPrice(dto.getSalePrice());
        saleDetail.setQuantity(dto.getQuantity());
        saleDetail.setProduct(product);
        saleDetail.setSale(sale);
        saleDetailRepo.save(saleDetail);
    }

    @Override
    public List<SaleDetail> getAll() {
        return saleDetailRepo.findAll();
    }

    @Override
    public DetailSaleDTO createDTO(Product product) {
        if (product == null) {
            DetailSaleDTO dto = new DetailSaleDTO();
            dto.setId(product.getId());
            dto.setDescription(product.getDescription());
            dto.setCategory(String.valueOf(product.getCategory()));
            dto.setUnit(String.valueOf(product.getUnit()));
            dto.setStock(product.getStock());
            dto.setSalePrice((int) (product.getSalePrice() * 1.19));
            dto.setTax((int) (product.getSalePrice() * 0.19));
            dto.setQuantity(1);
            dto.setTotal((int) (product.getSalePrice() * 1.19));
            return dto;
        }
        return null;
    }

    @Override
    public int deleteProduct(List<DetailSaleDTO> dtoList, Long id, int total) {
        Optional<DetailSaleDTO> optionalDto = dtoList.stream()
                .filter(dto -> Objects.equals(dto.getId(), id))
                .findFirst();

        if (optionalDto.isPresent()) {
            DetailSaleDTO dto = optionalDto.get();
            int price = dto.getTotal();
            total -= price;
            dtoList.remove(dto);
        }

        return Math.max(total, 0);
    }

    @Override
    public List<DetailSaleDTO> findDetailSaleBySaleId(Long id) {
        List<SaleDetail> detailSales = saleDetailRepo.findBySaleId(id);
        return detailSales.stream()
                .map(this::convertToDetailSaleDTO)
                .collect(Collectors.toList());
    }

    private DetailSaleDTO convertToDetailSaleDTO(SaleDetail saleDetail){
        DetailSaleDTO dto = new DetailSaleDTO();
        dto.setQuantity(saleDetail.getQuantity());
        dto.setDescription(saleDetail.getProduct().getDescription());
        dto.setTotal(saleDetail.getTotal());
        return dto;
    }
}
