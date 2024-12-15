import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PaymentCustomer } from '../models/Customer/payment-customer.model';

@Injectable({
  providedIn: 'root'
})
export class PaymentCustomerService {

  private apiUrl = 'http://localhost:8080/pagos';

  constructor(private httpClient: HttpClient) { }

  /**
   * Obtiene todos los pagos realizados
   * @returns Observable emite una lista de pagos
   */
  getAllPayments(): Observable<PaymentCustomer[]>{
    return this.httpClient.get<PaymentCustomer[]>(this.apiUrl);
  }

  /**
   * Obtienes lista de pagos de un client especifico
   * @param customerID ID del cliente por consultar
   * @returns Observable emite una lista con los pagos del cliente
   */
  getCustomerSelectedPayment(customerID: number): Observable<PaymentCustomer[]>{
    let params = new HttpParams().set('customerId', customerID.toString())
    return this.httpClient.get<PaymentCustomer[]>(`${this.apiUrl}/abonos`, {params});
  }
}
