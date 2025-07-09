package com.veloproweb.model.dto.purchase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplierResponseDTO {
    private String name;
    private String rut;
    private String email;
    private String phone;
}
