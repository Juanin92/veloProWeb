package com.veloProWeb.model.dto.customer;

import com.veloProWeb.model.Enum.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerResponseDTO {

    private Long id;
    private String name;
    private String surname;
    private String phone;
    private String email;
    private int debt;
    private int totalDebt;
    private PaymentStatus status;
    private boolean account;
}
