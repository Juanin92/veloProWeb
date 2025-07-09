package com.veloproweb.mapper;

import com.veloproweb.model.dto.customer.TicketResponseDTO;
import com.veloproweb.model.entity.customer.TicketHistory;
import org.springframework.stereotype.Component;

@Component
public class TicketHistoryMapper {

    public TicketResponseDTO toDto(TicketHistory ticketHistory){
        return TicketResponseDTO.builder()
                .id(ticketHistory.getId())
                .document(ticketHistory.getDocument())
                .total(ticketHistory.getTotal()).build();
    }
}
