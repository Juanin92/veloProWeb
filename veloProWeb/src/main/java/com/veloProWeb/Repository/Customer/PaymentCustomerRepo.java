package com.veloProWeb.Repository.Customer;

import com.veloProWeb.Model.Entity.Customer.PaymentCustomer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentCustomerRepo extends JpaRepository<PaymentCustomer,Long> {

    List<PaymentCustomer> findByCustomerId(Long id);
    List<PaymentCustomer> findByDocument(TicketHistory ticket);
}
