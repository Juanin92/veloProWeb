import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CustomerResponse } from '../../models/Entity/Customer/customer-response';
import { AuthService } from '../User/auth.service';
import { CustomerForm } from '../../models/Entity/Customer/customer-form';

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
  getCustomer(): Observable<CustomerResponse[]>{
    return this.httpClient.get<CustomerResponse[]>(this.apiUrl);
  }

  /**
   * Agregar un  nuevo cliente 
   * @param customer Cliente a agregar
   * @returns Observable que emite un mensaje de confirmación
   */
  addCustomer(customer: CustomerForm): Observable<{message: string}>{
    return this.httpClient.post<{message: string}>(`${this.apiUrl}/agregar`, customer, {headers: this.auth.getAuthHeaders()});
  }
  
  /**
   * Actualiza los datos de un cliente existente
   * @param customer Cliente con la nueva información
   * @returns Observable que emite un mensaje de confirmación
   */
  updateCustomer(customer: CustomerForm): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/actualizar`, customer, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Elimina (desactiva) un cliente
   * @param customer cliente seleccionado
   * @returns Observable que emite un mensaje de confirmación
   */
  deleteCustomer(customer: CustomerForm): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/eliminar`, customer, {headers: this.auth.getAuthHeaders()});
  }

  /**
   * Activa un cliente 
   * @param customer cliente seleccionado
   * @returns Observable que emite un mensaje de confirmación
   */
  activeCustomer(customer: CustomerForm): Observable<{message: string}>{
    return this.httpClient.put<{message: string}>(`${this.apiUrl}/activar`, customer, {headers: this.auth.getAuthHeaders()});
  }
}
