package com.veloProWeb.Repository.Customer;

import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketHistoryRepo extends JpaRepository<TicketHistory, Long> {

    List<TicketHistory> findByCustomerId(Long id);
}
