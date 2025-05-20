package com.veloProWeb.mapper;

import com.veloProWeb.model.dto.purchase.PurchaseRequestDTO;
import com.veloProWeb.model.entity.Purchase.Purchase;
import com.veloProWeb.model.entity.Purchase.Supplier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PurchaseMapper {

    public Purchase toEntity(PurchaseRequestDTO dto, Supplier supplier){
        return Purchase.builder()
                .document(dto.getDocument())
                .documentType(dto.getDocumentType())
                .iva(dto.getTax())
                .purchaseTotal(dto.getTotal())
                .date(LocalDate.now())
                .supplier(supplier)
                .build();
    }
}
