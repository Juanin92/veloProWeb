package com.veloProWeb.mapper;

import com.veloProWeb.model.dto.customer.TicketResponseDTO;
import com.veloProWeb.model.entity.customer.TicketHistory;
import org.springframework.stereotype.Component;

@Component
public class TicketHistoryMapper {

    public TicketResponseDTO toDto(TicketHistory ticketHistory){
        return TicketResponseDTO.builder()
                .document(ticketHistory.getDocument())
                .total(ticketHistory.getTotal())
                .status(ticketHistory.isStatus())
                .customer(String.format("%s %s", ticketHistory.getCustomer().getName(),
                        ticketHistory.getCustomer().getSurname())).build();
    }
}
