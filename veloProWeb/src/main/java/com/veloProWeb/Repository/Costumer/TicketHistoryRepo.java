package com.veloProWeb.Repository.Costumer;

import com.veloProWeb.Model.Entity.Costumer.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketHistoryRepo extends JpaRepository<TicketHistory, Long> {

    List<TicketHistory> findByCostumerId(Long id);
}
