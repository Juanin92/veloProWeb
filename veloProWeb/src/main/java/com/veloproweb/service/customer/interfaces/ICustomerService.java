package com.veloproweb.service.customer.interfaces;

import com.veloproweb.model.dto.customer.CustomerRequestDTO;
import com.veloproweb.model.dto.customer.CustomerResponseDTO;
import com.veloproweb.model.entity.customer.Customer;

import java.util.List;

public interface ICustomerService {
    void addNewCustomer(CustomerRequestDTO dto);
    List<CustomerResponseDTO> getAll();
    void delete(CustomerRequestDTO dto);
    void activeCustomer(CustomerRequestDTO dto);
    void paymentDebt(Customer customer, String amount);
    void statusAssign(Customer customer);
    void addSaleToCustomer(Customer customer);
    void updateTotalDebt(Customer customer);
    void updateCustomer(CustomerRequestDTO dto);
    Customer getCustomerById(Long ID);
}
