package com.veloProWeb.mapper;

import com.veloProWeb.model.dto.customer.PaymentRequestDTO;
import com.veloProWeb.model.dto.customer.PaymentResponseDTO;
import com.veloProWeb.model.entity.customer.PaymentCustomer;
import org.springframework.stereotype.Component;

@Component
public class PaymentCustomerMapper {

    public PaymentCustomer toEntity(PaymentRequestDTO dto){
        return PaymentCustomer.builder()
                .amount(dto.getAmount())
                .comment(dto.getComment())
                .build();
    }

    public PaymentResponseDTO toDto(PaymentCustomer paymentCustomer){
        return PaymentResponseDTO.builder()
                .amount(paymentCustomer.getAmount())
                .comment(paymentCustomer.getComment())
                .date(paymentCustomer.getDate())
                .document(paymentCustomer.getDocument().getDocument())
                .build();
    }
}
