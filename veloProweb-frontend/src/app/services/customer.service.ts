import { HttpClient, HttpHandler, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Customer } from '../models/Customer/customer.model';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  private apiUrl = 'http://localhost:8080/clientes';

  constructor(private httpClient: HttpClient) { }

  getCustomer(): Observable<Customer[]>{
    return this.httpClient.get<Customer[]>(this.apiUrl);
  }

  addCustomer(customer: Customer): Observable<string>{
    return this.httpClient.post<string>(`${this.apiUrl}/agregar`, customer);
  }

  updateCustomer(customer: Customer): Observable<Customer>{
    return this.httpClient.put<Customer>(`${this.apiUrl}/actualizar`, customer);
  }

  deleteCustomer(customer: Customer): Observable<string>{
    return this.httpClient.put<string>(`${this.apiUrl}/eliminar`, customer);
  }
}
