package com.veloProWeb.mapper;

import com.veloProWeb.model.dto.customer.TicketResponseDTO;
import com.veloProWeb.model.entity.customer.TicketHistory;
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
