package com.veloProWeb.service.customer.interfaces;

import com.veloProWeb.model.dto.customer.TicketResponseDTO;
import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.model.entity.customer.TicketHistory;

import java.util.List;

public interface ITicketHistoryService {

    void AddTicketToCustomer(Customer customer, int total);
    List<TicketHistory> getByCustomerId(Long id);
    List<TicketResponseDTO> getByCustomerIdDTO(Long id);
    void updateStatus(TicketHistory  ticket);
    TicketHistory getTicketByID(Long Id);
}
