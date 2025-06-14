package com.veloproweb.service.customer.interfaces;

import com.veloproweb.model.dto.customer.PaymentRequestDTO;
import com.veloproweb.model.dto.customer.PaymentResponseDTO;

import java.util.List;

public interface IPaymentCustomerService {
    void createPaymentProcess(PaymentRequestDTO dto);
    List<PaymentResponseDTO> getAll();
    List<PaymentResponseDTO> getCustomerSelected(Long id);
}
