package com.veloProWeb.service.customer.interfaces;

import com.veloProWeb.model.dto.customer.PaymentRequestDTO;
import com.veloProWeb.model.entity.customer.PaymentCustomer;

import java.util.List;

public interface IPaymentCustomerService {
    void createPaymentProcess(PaymentRequestDTO dto);
    List<PaymentCustomer> getAll();
    List<PaymentCustomer> getCustomerSelected(Long id);
}
