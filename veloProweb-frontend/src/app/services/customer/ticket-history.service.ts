import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TicketHistory } from '../../models/Entity/Customer/ticket-history';
import { AuthService } from '../User/auth.service';

@Injectable({
  providedIn: 'root',
})
export class TicketHistoryService {
  private apiUrl = 'http://localhost:8080/Tickets';

  constructor(private httpClient: HttpClient, private auth: AuthService) {}

  /**
   * Obtiene los ticket de un cliente seleccionado
   * @param customerId - Identificador del cliente
   * @returns - Observable emite una lista de tickets
   */
  getListTicketByCustomer(customerId: number): Observable<TicketHistory[]> {
    let params = new HttpParams().set('customerId', customerId.toString());
    return this.httpClient.get<TicketHistory[]>(this.apiUrl, { params });
  }
}
