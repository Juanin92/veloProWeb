import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TicketHistory } from '../models/Customer/ticket-history.model';
import { TicketRequestDTO } from '../models/DTO/ticket-request-dto';
import { Customer } from '../models/Customer/customer.model';

@Injectable({
  providedIn: 'root'
})
export class TicketHistoryService {

  private apiUrl = 'http://localhost:8080/Tickets';

  constructor(private httpClient: HttpClient) { }

  getListTicketByCustomer(customerId: number): Observable<TicketHistory[]>{
    let params = new HttpParams().set('customerId', customerId.toString());
    return this.httpClient.get<TicketHistory[]>(this.apiUrl, {params});
  }

  createTicketToCustomer(ticketRequestDTO: TicketRequestDTO): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(this.apiUrl, ticketRequestDTO);
  }

  valideTicketByCustomer(customer: Customer): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/validar`, customer);
  }

  updateStatus(ticket: TicketHistory): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/actualizar-estado`, ticket);
  }
}
