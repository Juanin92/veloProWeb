package com.veloProWeb.service.customer.interfaces;

import com.veloProWeb.model.dto.customer.PaymentRequestDTO;
import com.veloProWeb.model.dto.customer.PaymentResponseDTO;

import java.util.List;

public interface IPaymentCustomerService {
    void createPaymentProcess(PaymentRequestDTO dto);
    List<PaymentResponseDTO> getAll();
    List<PaymentResponseDTO> getCustomerSelected(Long id);
}
