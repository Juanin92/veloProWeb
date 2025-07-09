package com.veloproweb.service.customer.interfaces;

import com.veloproweb.model.dto.customer.TicketResponseDTO;
import com.veloproweb.model.entity.customer.Customer;
import com.veloproweb.model.entity.customer.TicketHistory;

import java.util.List;

public interface ITicketHistoryService {

    void addTicketToCustomer(Customer customer, int total);
    List<TicketHistory> getByCustomerId(Long id);
    List<TicketResponseDTO> getByCustomerIdDTO(Long id);
    void updateStatus(TicketHistory  ticket);
    TicketHistory getTicketById(Long Id);
}
