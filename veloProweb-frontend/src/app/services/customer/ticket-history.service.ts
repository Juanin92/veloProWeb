import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TicketHistory } from '../../models/Entity/Customer/ticket-history.model';
import { TicketRequestDTO } from '../../models/DTO/ticket-request-dto';
import { Customer } from '../../models/Entity/Customer/customer.model';

@Injectable({
  providedIn: 'root'
})
export class TicketHistoryService {

  private apiUrl = 'http://localhost:8080/Tickets';

  constructor(private httpClient: HttpClient) { }

  /**
   * Obtiene los ticket de un cliente seleccionado
   * @param customerId - Identificador del cliente
   * @returns - Observable emite una lista de tickets
   */
  getListTicketByCustomer(customerId: number): Observable<TicketHistory[]>{
    let params = new HttpParams().set('customerId', customerId.toString());
    return this.httpClient.get<TicketHistory[]>(this.apiUrl, {params});
  }

  /**
   * crea un nuevo ticket para un cliente
   * @param ticketRequestDTO - Objeto que contiene los detalles del ticket
   * @returns - Observable emite un mensaje de confirmación
   */
  createTicketToCustomer(ticketRequestDTO: TicketRequestDTO): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(this.apiUrl, ticketRequestDTO);
  }

  /**
   * Valida el ticket de un cliente
   * @param customer - Cliente seleccionado
   * @returns - Observable emite un mensaje de confirmación
   */
  valideTicketByCustomer(customer: Customer): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/validar`, customer);
  }

  /**
   * Actualiza el estado de un ticket
   * @param ticket - ticket seleccionado
   * @returns - Observable emite un mensaje de confirmación
   */
  updateStatus(ticket: TicketHistory): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/actualizar-estado`, ticket);
  }
}
