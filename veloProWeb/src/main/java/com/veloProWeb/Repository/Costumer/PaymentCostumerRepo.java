package com.veloProWeb.Repository.Costumer;

import com.veloProWeb.Model.Entity.Costumer.PaymentCostumer;
import com.veloProWeb.Model.Entity.Costumer.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentCostumerRepo extends JpaRepository<PaymentCostumer,Long> {

    List<PaymentCostumer> findByCostumerId(Long id);
    List<PaymentCostumer> findByDocument(TicketHistory ticket);
}
