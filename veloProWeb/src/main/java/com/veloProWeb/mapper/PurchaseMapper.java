package com.veloProWeb.mapper;

import com.veloProWeb.model.dto.purchase.PurchaseDetailRequestDTO;
import com.veloProWeb.model.dto.purchase.PurchaseDetailResponseDTO;
import com.veloProWeb.model.dto.purchase.PurchaseRequestDTO;
import com.veloProWeb.model.dto.purchase.PurchaseResponseDTO;
import com.veloProWeb.model.entity.Purchase.Purchase;
import com.veloProWeb.model.entity.Purchase.PurchaseDetail;
import com.veloProWeb.model.entity.Purchase.Supplier;
import com.veloProWeb.model.entity.product.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
public class PurchaseMapper {

    public Purchase toPurchaseEntity(PurchaseRequestDTO dto, Supplier supplier){
        return Purchase.builder()
                .document(dto.getDocument())
                .documentType(dto.getDocumentType())
                .iva(dto.getTax())
                .purchaseTotal(dto.getTotal())
                .date(LocalDate.now())
                .supplier(supplier)
                .build();
    }

    public PurchaseDetail toPurchaseDetailEntity(PurchaseDetailRequestDTO dto, Product product, Purchase purchase){
        return PurchaseDetail.builder()
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .tax(dto.getTax())
                .total(dto.getTotal())
                .product(product)
                .purchase(purchase)
                .build();
    }

    public PurchaseResponseDTO toPurchaseResponseDTO(Purchase purchase){
        return PurchaseResponseDTO.builder()
                .document(purchase.getDocument())
                .documentType(purchase.getDocumentType())
                .date(purchase.getDate())
                .iva(purchase.getIva())
                .purchaseTotal(purchase.getPurchaseTotal())
                .supplier(purchase.getSupplier().getName())
                .detailsList(
                        purchase.getPurchaseDetails().stream()
                        .map(this::toDetailPurchaseResponseDTO)
                                .collect(Collectors.toList())
                )
                .build();
    }

    public PurchaseDetailResponseDTO toDetailPurchaseResponseDTO(PurchaseDetail purchaseDetail){
        return PurchaseDetailResponseDTO.builder()
                .descriptionProduct(purchaseDetail.getProduct().getDescription())
                .price(purchaseDetail.getPrice())
                .tax(purchaseDetail.getTax())
                .total(purchaseDetail.getTotal())
                .quantity(purchaseDetail.getQuantity())
                .build();
    }
}
