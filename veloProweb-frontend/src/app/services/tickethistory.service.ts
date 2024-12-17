import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TicketHistory } from '../models/Customer/ticket-history.model';

@Injectable({
  providedIn: 'root'
})
export class TickethistoryService {

  private apiUrl = 'http://localhost:8080/Tickets';

  constructor(private httpClient: HttpClient) { }

  getListTicketByCustomer(customerId: number): Observable<TicketHistory[]>{
    let params = new HttpParams().set('customerId', customerId.toString());
    return this.httpClient.get<TicketHistory[]>(this.apiUrl, {params});
  }

  createTicketToCustomer(): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(this.apiUrl);
  }
}
