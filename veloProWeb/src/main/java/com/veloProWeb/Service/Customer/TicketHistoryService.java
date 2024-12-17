package com.veloProWeb.Service.Customer;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import com.veloProWeb.Model.Enum.PaymentStatus;
import com.veloProWeb.Repository.Customer.TicketHistoryRepo;
import com.veloProWeb.Service.Customer.Interfaces.ICustomerService;
import com.veloProWeb.Service.Customer.Interfaces.ITicketHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class TicketHistoryService implements ITicketHistoryService {

    @Autowired private TicketHistoryRepo ticketHistoryRepo;
    @Autowired private ICustomerService customerService;
    private LocalDate lastValidationDate;

    /**
     * Crea un ticket para un cliente específico
     * @param customer cliente a quien se le crea el ticket
     * @param number número del ticket que generará el número del documento
     * @param total valor del monto del ticket
     * @param date fecha de creación del ticket
     */
    @Override
    public void AddTicketToCustomer(Customer customer, Long number, int total, LocalDate date) {
        TicketHistory ticket = new TicketHistory();
        String name = "BO00" + number;
        ticket.setTotal(total);
        ticket.setDocument(name);
        ticket.setStatus(false);
        ticket.setDate(date);
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

    /**
     * Válida los tickets de un cliente y actualiza el estado de la deuda.
     *
     * <p>Se encarga de validar si {@code lastValidationDate} es nulo o si la fecha no es igual al día actual.
     * Si el estado del ticket es falso y el método {@code validateDate} devuelve verdadero, se actualiza
     * el estado del cliente a {@code PaymentStatus.VENCIDA}.
     * Asigna la fecha actual a la variable {@code lastValidationDate} cuando se validan los tickets.</p>
     *
     * @param customer el cliente cuyos tickets se deben validar
     */
    @Override
    public void valideTicketByCustomer(Customer customer) {
        LocalDate today = LocalDate.now();
        if (lastValidationDate == null || !lastValidationDate.equals(today)) {
            List<TicketHistory> tickets = getByCustomerId(customer.getId());
            for (TicketHistory ticket : tickets) {
                if (!ticket.isStatus() && validateDate(ticket)) {
                    customer.setStatus(PaymentStatus.VENCIDA);
                    customerService.updateTotalDebt(customer);
                }
            }
            lastValidationDate = today;
        }
    }

    /**
     * Actualiza el estado del ticket seleccionado a verdadero
     * @param ticket ticket para actualizar su estado
     */
    @Override
    public void updateStatus(TicketHistory ticket) {
        ticket.setStatus(true);
        ticketHistoryRepo.save(ticket);
    }

    /**
     * Válida la fecha de un ticket y actualiza la fecha de notificación si es necesario.
     * Verifica si el estado del ticket es falso y si han pasado más de 30 días desde la fecha del ticket.
     * También verifica si la fecha de notificación es nula o si han pasado más de 15 días desde la fecha de última notificación.
     * Si se cumplen estas condiciones, actualiza la fecha de notificación del ticket a la fecha actual y guarda el ticket en el repositorio.
     *
     * @param ticket ticket que se debe validar
     * @return verdadero si el ticket fue validado y la fecha de notificación fue actualizada, falso en caso contrario
     */
    public boolean validateDate(TicketHistory ticket) {
        LocalDate now = LocalDate.now();
        LocalDate ticketDate = ticket.getDate();
        LocalDate notificationDate = ticket.getNotificationsDate();

        if (ticket.isStatus()) {
            return false;
        }
        if (ChronoUnit.DAYS.between(ticketDate, now) > 30 &&
                (notificationDate == null || ChronoUnit.DAYS.between(notificationDate, now) > 15)) {
            ticket.setNotificationsDate(now);
            ticketHistoryRepo.save(ticket);
            return true;
        }
        return false;
    }

}
