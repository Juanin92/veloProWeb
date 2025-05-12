package com.veloProWeb.service.customer;

import com.veloProWeb.exceptions.Customer.TicketAlreadyPaidException;
import com.veloProWeb.exceptions.Customer.TicketNotFoundException;
import com.veloProWeb.mapper.TicketHistoryMapper;
import com.veloProWeb.model.dto.customer.TicketResponseDTO;
import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.model.entity.customer.TicketHistory;
import com.veloProWeb.repository.customer.TicketHistoryRepo;
import com.veloProWeb.service.customer.interfaces.ITicketHistoryService;
import com.veloProWeb.util.IdentifyDocumentGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class TicketHistoryService implements ITicketHistoryService {

    private final TicketHistoryRepo ticketHistoryRepo;
    private final TicketHistoryMapper mapper;

    /**
     * Crea un ticket para un cliente específico
     * @param customer - nombre del cliente
     * @param total - total del ticket
     */
    @Transactional
    @Override
    public void addTicketToCustomer(Customer customer, int total) {
        String lastDocument = getLastTicketCreated().getDocument();
        TicketHistory ticket = new TicketHistory();
        ticket.setTotal(total);
        ticket.setDocument(IdentifyDocumentGenerator.generateIdentifyDocumentTicket(lastDocument));
        ticket.setStatus(false);
        ticket.setDate(LocalDate.now());
        ticket.setCustomer(customer);
        ticketHistoryRepo.save(ticket);
    }

    /**
     * Obtiene una lista de tickets para un cliente por su ID.
     * Filtra los tickets que estén con su estado false y luego
     * se ordena de menor a mayor los tickets
     * @param id ID del cliente
     * @return lista filtrada de tickets del cliente
     */
    @Override
    public List<TicketHistory> getByCustomerId(Long id) {
        return ticketHistoryRepo.findByCustomerId(id)
                .stream()
                .filter(ticketHistory -> !ticketHistory.isStatus())
                .sorted(Comparator.comparing(TicketHistory::getTotal))
                .toList();
    }

    @Override
    public List<TicketResponseDTO> getByCustomerIdDTO(Long id) {
        return ticketHistoryRepo.findByCustomerId(id)
                .stream()
                .filter(ticketHistory -> !ticketHistory.isStatus())
                .sorted(Comparator.comparing(TicketHistory::getTotal))
                .map(mapper::toDto)
                .toList();
    }

    /**
     * Actualiza el estado del ticket seleccionado a verdadero
     * @param ticket ticket para actualizar su estado
     */
    @Transactional
    @Override
    public void updateStatus(TicketHistory ticket) {
        if (!ticket.isStatus()) {
            ticket.setStatus(true);
            ticketHistoryRepo.save(ticket);
        }else {
            throw new TicketAlreadyPaidException(String.format("El ticket %s ya fue pagado", ticket.getDocument()));
        }
    }

    /**
     * Obtener un ticket específico
     * @param Id - Identificador de ticket a buscar
     * @return - Ticket encontrado o excepción depende del caso
     */
    @Override
    public TicketHistory getTicketById(Long Id) {
        return ticketHistoryRepo.findById(Id).orElseThrow(() -> new TicketNotFoundException("Ticket no encontrado"));
    }

    private TicketHistory getLastTicketCreated() {
        return ticketHistoryRepo.findLastCreated();
    }
}
