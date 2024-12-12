package com.veloProWeb.Service.Customer;

import com.veloProWeb.Model.Entity.Customer.Customer;

import java.util.List;

public interface ICustomerService {
    void addNewCustomer(Customer customer);
    List<Customer> getAll();
    void delete(Customer customer);
    void activeCustomer(Customer customer);
    void paymentDebt(Customer customer, String amount);
    void statusAssign(Customer customer);
    void addSaleToCustomer(Customer customer);
    void updateTotalDebt(Customer customer);
    void updateCustomer(Customer customer);
}
