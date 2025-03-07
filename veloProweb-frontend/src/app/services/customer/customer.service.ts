import { HttpClient, HttpHandler, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Customer } from '../../models/Entity/Customer/customer.model';
import { AuthService } from '../User/auth.service';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  private apiUrl = 'http://localhost:8080/clientes';

  constructor(private httpClient: HttpClient, private auth: AuthService) {
   }

  /**
   * Obtiene una lista de todos los clientes desde el endpoint
   * @returns observable que emite una lista de clientes
   */
  getCustomer(): Observable<Customer[]>{
    return this.httpClient.get<Customer[]>(this.apiUrl);
  }

  /**
   * Agregar un  nuevo cliente 
   * @param customer Cliente a agregar
   * @returns Observable que emite un mensaje de confirmación
   */
  addCustomer(customer: Customer): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(`${this.apiUrl}/agregar`, customer);
  }
  
  /**
   * Actualiza los datos de un cliente existente
   * @param customer Cliente con la nueva información
   * @returns Observable que emite un mensaje de confirmación
   */
  updateCustomer(customer: Customer): Observable<string>{
    return this.httpClient.put<string>(`${this.apiUrl}/actualizar`, customer, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Elimina (desactiva) un cliente
   * @param customer cliente seleccionado
   * @returns Observable que emite un mensaje de confirmación
   */
  deleteCustomer(customer: Customer): Observable<string>{
    return this.httpClient.put<string>(`${this.apiUrl}/eliminar`, customer);
  }

  /**
   * Activa un cliente 
   * @param customer cliente seleccionado
   * @returns Observable que emite un mensaje de confirmación
   */
  activeCustomer(customer: Customer): Observable<string>{
    return this.httpClient.put<string>(`${this.apiUrl}/activar`, customer);
  }
}
