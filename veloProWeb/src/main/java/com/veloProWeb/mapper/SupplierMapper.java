package com.veloProWeb.mapper;

import com.veloProWeb.model.dto.purchase.SupplierRequestDTO;
import com.veloProWeb.model.dto.purchase.SupplierResponseDTO;
import com.veloProWeb.model.entity.Purchase.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public Supplier toEntity(SupplierRequestDTO dto){
        return Supplier.builder()
                .rut(dto.getRut())
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build();
    }

    public SupplierResponseDTO responseDTO(Supplier supplier){
        return SupplierResponseDTO.builder()
                .rut(supplier.getRut())
                .name(supplier.getName())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .build();
    }

    public void updateSupplierFromDto(Supplier supplier, SupplierRequestDTO dto){
        supplier.setId(supplier.getId());
        supplier.setRut(dto.getRut());
        supplier.setName(dto.getName());
        supplier.setEmail(dto.getEmail());
        supplier.setPhone(dto.getPhone());
    }
}
