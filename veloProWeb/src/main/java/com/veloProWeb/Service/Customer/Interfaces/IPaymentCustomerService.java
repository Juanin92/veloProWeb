package com.veloProWeb.Service.Customer.Interfaces;

import com.veloProWeb.Model.DTO.PaymentRequestDTO;
import com.veloProWeb.Model.Entity.Customer.PaymentCustomer;

import java.util.List;

public interface IPaymentCustomerService {
    void createPaymentProcess(PaymentRequestDTO dto);
    List<PaymentCustomer> getAll();
    List<PaymentCustomer> getCustomerSelected(Long id);
}
