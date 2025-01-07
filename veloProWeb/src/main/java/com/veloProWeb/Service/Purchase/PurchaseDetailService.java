package com.veloProWeb.Service.Purchase;

import com.veloProWeb.Model.DTO.DetailPurchaseDTO;
import com.veloProWeb.Model.Entity.Product.Product;
import com.veloProWeb.Model.Entity.Purchase.Purchase;
import com.veloProWeb.Model.Entity.Purchase.PurchaseDetail;
import com.veloProWeb.Repository.Purchase.PurchaseDetailRepo;
import com.veloProWeb.Service.Purchase.Interfaces.IPurchaseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PurchaseDetailService implements IPurchaseDetailService {

    @Autowired private PurchaseDetailRepo purchaseDetailRepo;

    @Override
    public void createDetailPurchase(DetailPurchaseDTO dto, Purchase purchase, Product product) {
        PurchaseDetail purchaseDetail =  new PurchaseDetail();
        purchaseDetail.setPrice(dto.getPrice());
        purchaseDetail.setQuantity(dto.getQuantity());
        purchaseDetail.setTax(dto.getTax());
        purchaseDetail.setTotal(dto.getTotal());
        purchaseDetail.setProduct(product);
        purchaseDetail.setPurchase(purchase);
        purchaseDetailRepo.save(purchaseDetail);
    }

    @Override
    public List<PurchaseDetail> getAll() {
        return purchaseDetailRepo.findAll();
    }

    @Override
    public DetailPurchaseDTO createDTO(Product product) {
        if (product != null) {
            DetailPurchaseDTO dto =new DetailPurchaseDTO();
            dto.setIdProduct(product.getId());
            dto.setBrand(product.getBrand().getName());
            dto.setDescription(product.getDescription());
            dto.setPrice(0);
            dto.setTax(0);
            dto.setTotal(0);
            dto.setQuantity(0);
            return dto;
        }
        return null;
    }

    @Override
    public boolean deleteProduct(Long id, List<DetailPurchaseDTO> dtoList) {
        Optional<DetailPurchaseDTO> optionalDto = dtoList.stream()
                .filter(dto -> Objects.equals(dto.getIdProduct(), id))
                .findFirst();

        if (optionalDto.isPresent()) {
            dtoList.remove(optionalDto.get());
            return true;
        }
        return false;
    }
}
