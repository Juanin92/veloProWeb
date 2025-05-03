package com.veloProWeb.service.customer.interfaces;

import com.veloProWeb.model.dto.customer.CustomerDTO;
import com.veloProWeb.model.dto.customer.CustomerResponseDTO;
import com.veloProWeb.model.entity.customer.Customer;

import java.util.List;

public interface ICustomerService {
    void addNewCustomer(CustomerDTO dto);
    List<CustomerResponseDTO> getAll();
    void delete(CustomerDTO dto);
    void activeCustomer(CustomerDTO dto);
    void paymentDebt(Customer customer, String amount);
    void statusAssign(Customer customer);
    void addSaleToCustomer(Customer customer);
    void updateTotalDebt(Customer customer);
    void updateCustomer(CustomerDTO dto);
    Customer getCustomerById(Long ID);
}
