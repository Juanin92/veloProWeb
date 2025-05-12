package com.veloProWeb.repository.customer;

import com.veloProWeb.model.entity.customer.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketHistoryRepo extends JpaRepository<TicketHistory, Long> {

    List<TicketHistory> findByCustomerId(Long id);

    @Query(nativeQuery = true, value = "SELECT * FROM ticket_history ORDER BY date DESC, id DESC LIMIT 1")
    TicketHistory findLastCreated();
}
