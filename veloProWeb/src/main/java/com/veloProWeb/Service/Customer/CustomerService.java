package com.veloProWeb.Service.Customer;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Enum.PaymentStatus;
import com.veloProWeb.Repository.Customer.CustomerRepo;
import com.veloProWeb.Validation.CustomerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService implements ICustomerService {

    @Autowired private CustomerRepo customerRepo;
    @Autowired private CustomerValidator validator;

    @Override
    public void addNewCustomer(Customer customer) {
        Customer customerDB = getCostumerCreated(customer.getName(), customer.getSurname());
        if (customerDB != null){
            throw new IllegalArgumentException("Cliente Existente: Hay registro de este cliente.");
        }else {
            if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
                customer.setEmail("x@x.xxx");
            }
            validator.validate(customer);
            customer.setAccount(true);
            customer.setStatus(PaymentStatus.NULO);
            customer.setTotalDebt(0);
            customer.setDebt(0);
            customer.setName(capitalize(customer.getName()));
            customer.setSurname(capitalize(customer.getSurname()));
            customerRepo.save(customer);
        }
    }

    @Override
    public void updateCustomer(Customer customer) {
        Customer customerDB = getCostumerCreated(customer.getName(), customer.getSurname());
        if (customerDB != null && !customerDB.getId().equals(customer.getId())){
            throw new IllegalArgumentException("Cliente Existente: Hay registro de este cliente.");
        }else {
            if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
                customer.setEmail("x@x.xxx");
            }
            validator.validate(customer);
            customer.setName(capitalize(customer.getName()));
            customer.setSurname(capitalize(customer.getSurname()));
            customerRepo.save(customer);
        }
    }

    @Override
    public List<Customer> getAll() {
        return customerRepo.findAll();
    }

    @Override
    public void delete(Customer customer) {
        if (customer.isAccount()){
            customer.setAccount(false);
            customerRepo.save(customer);
        }else {
            throw new IllegalArgumentException("Cliente ya ha sido eliminado anteriormente.");
        }
    }

    @Override
    public void activeCustomer(Customer customer) {
        if (customer == null || customer.getId() == null) {
            throw new IllegalArgumentException("Cliente no válido, Null");
        }
        if (!customer.isAccount()){
            customer.setAccount(true);
            customerRepo.save(customer);
        }else{
            throw new IllegalArgumentException("El cliente tiene su cuenta activada");
        }
    }

    @Override
    public void paymentDebt(Customer customer, String amount) {
        int number = Integer.parseInt(amount);
        validator.validateValuePayment(number, customer);
        customer.setDebt(customer.getDebt() - number);
        customerRepo.save(customer);
        statusAssign(customer);
    }

    @Override
    public void statusAssign(Customer customer) {
        if (customer.getTotalDebt() == 0) {
            customer.setStatus(PaymentStatus.NULO);
        }else if (customer.getDebt() == 0 && customer.getTotalDebt() > 0) {
            customer.setStatus(PaymentStatus.PAGADA);
        } else if (customer.getDebt() <= (customer.getTotalDebt() / 2) && customer.getDebt() > 0) {
            customer.setStatus(PaymentStatus.PARCIAL);
        } else if (customer.getDebt() > 0 && customer.getTotalDebt() > 0) {
            customer.setStatus(PaymentStatus.PENDIENTE);
        }
        customerRepo.save(customer);
    }

    @Override
    public void addSaleToCustomer(Customer customer) {
        customer.setDebt(customer.getTotalDebt());
        customerRepo.save(customer);
        statusAssign(customer);
    }

    @Override
    public void updateTotalDebt(Customer customer) {
        customerRepo.save(customer);
    }

    private Customer getCostumerCreated(String name, String surname) {
        Optional<Customer> customerOptional = customerRepo.findBySimilarNameAndSurname(capitalize(name), capitalize(surname));
        return customerOptional.orElse(null);
    }

    private String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String[] words = value.split(" ");
        StringBuilder capitalized = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(word.substring(0, 1).toUpperCase());
                capitalized.append(word.substring(1).toLowerCase());
                capitalized.append(" ");
            }
        }
        return capitalized.toString().trim();
    }
}
