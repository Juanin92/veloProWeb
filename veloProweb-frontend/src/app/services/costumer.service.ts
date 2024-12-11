import { HttpClient, HttpHandler, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Costumer } from '../models/Costumer/costumer.model';

@Injectable({
  providedIn: 'root'
})
export class CostumerService {

  private apiUrl = 'http://localhost:8080/clientes';

  constructor(private httpClient: HttpClient) { }

  getCostumer(): Observable<Costumer[]>{
    return this.httpClient.get<Costumer[]>(this.apiUrl);
  }

  addCostumer(costumer: Costumer): Observable<string>{
    return this.httpClient.post<string>(`${this.apiUrl}/agregar`, costumer);
  }

  updateCostumer(costumer: Costumer): Observable<Costumer>{
    return this.httpClient.put<Costumer>(`${this.apiUrl}/actualizar`, costumer);
  }
}
