package com.veloProWeb.repository.customer;

import com.veloProWeb.model.entity.customer.PaymentCustomer;
import com.veloProWeb.model.entity.customer.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentCustomerRepo extends JpaRepository<PaymentCustomer,Long> {

    List<PaymentCustomer> findByCustomerId(Long id);
    List<PaymentCustomer> findByDocument(TicketHistory ticket);
}
