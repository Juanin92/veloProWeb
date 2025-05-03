package com.veloProWeb.service.customer;

import com.veloProWeb.model.entity.customer.Customer;
import com.veloProWeb.model.entity.customer.TicketHistory;
import com.veloProWeb.repository.customer.TicketHistoryRepo;
import com.veloProWeb.service.customer.interfaces.ICustomerService;
import com.veloProWeb.service.customer.interfaces.ITicketHistoryService;
import com.veloProWeb.util.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
public class TicketHistoryService implements ITicketHistoryService {

    @Autowired private TicketHistoryRepo ticketHistoryRepo;
    @Autowired private ICustomerService customerService;
    @Autowired @Lazy private EmailService emailService;
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
        String name = "BO_" + number;
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
     * Actualiza el estado del ticket seleccionado a verdadero
     * @param ticket ticket para actualizar su estado
     */
    @Override
    public void updateStatus(TicketHistory ticket) {
        ticket.setStatus(true);
        ticketHistoryRepo.save(ticket);
    }

    /**
     * Obtener un ticket específico
     * @param Id - Identificador de ticket a buscar
     * @return - Ticket encontrado o excepción depende del caso
     */
    @Override
    public TicketHistory getTicketByID(Long Id) {
        return ticketHistoryRepo.findById(Id).orElseThrow(() -> new IllegalArgumentException("Ticket no encontrado"));
    }

    /**
     * Válida los tickets de todos los clientes diariamente a las 12:00 PM.
     * Si la fecha de validación es diferente a la fecha actual, se valida cada ticket
     * y se envía un correo electrónico al cliente si el ticket está vencido.
     */
//    @Scheduled(cron = "0 0 12 * * ?")
//    public void validateTicketsDaily() {
//        LocalDate today = LocalDate.now();
//        if (lastValidationDate == null || !lastValidationDate.equals(today)) {
//            List<customer> customers = customerService.getAll();
//            for (customer customer : customers) {
//                List<TicketHistory> tickets = getByCustomerId(customer.getId());
//                for (TicketHistory ticket : tickets) {
//                    if (!ticket.isStatus() && validateDate(ticket)) {
//                        customer.setStatus(PaymentStatus.VENCIDA);
//                        customerService.updateTotalDebt(customer);
//                        emailService.sendOverdueTicketNotification(customer, ticket);
//                    }
//                }
//            }
//            lastValidationDate = today;
//        }
//    }

    /**
     * Válida la fecha de un ticket y actualiza la fecha de notificación si es necesario.
     * Verifica si el estado del ticket es falso y si han pasado más de 30 días desde la fecha del ticket.
     * También verifica si la fecha de notificación es nula o si han pasado más de 15 días desde la fecha de última notificación.
     * Si se cumplen estas condiciones, actualiza la fecha de notificación del ticket a la fecha actual y guarda el ticket en el repositorio.
     *
     * @param ticket ticket que se debe validar
     * @return verdadero si el ticket fue validado y la fecha de notificación fue actualizada, falso en caso contrario
     */
    private boolean validateDate(TicketHistory ticket) {
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
