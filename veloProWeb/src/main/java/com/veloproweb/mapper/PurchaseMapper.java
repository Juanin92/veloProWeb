package com.veloproweb.mapper;

import com.veloproweb.model.dto.purchase.PurchaseDetailRequestDTO;
import com.veloproweb.model.dto.purchase.PurchaseDetailResponseDTO;
import com.veloproweb.model.dto.purchase.PurchaseRequestDTO;
import com.veloproweb.model.dto.purchase.PurchaseResponseDTO;
import com.veloproweb.model.entity.purchase.Purchase;
import com.veloproweb.model.entity.purchase.PurchaseDetail;
import com.veloproweb.model.entity.purchase.Supplier;
import com.veloproweb.model.entity.product.Product;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PurchaseMapper {

    public Purchase toPurchaseEntity(PurchaseRequestDTO dto, Supplier supplier){
        return Purchase.builder()
                .document(dto.getDocument())
                .documentType(dto.getDocumentType())
                .date(dto.getDate())
                .iva(dto.getTax())
                .purchaseTotal(dto.getTotal())
                .date(dto.getDate())
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
