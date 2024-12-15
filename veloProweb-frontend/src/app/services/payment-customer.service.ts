import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PaymentCustomer } from '../models/Customer/payment-customer.model';

@Injectable({
  providedIn: 'root'
})
export class PaymentCustomerService {

  private apiUrl = 'http://localhost:8080/pago';

  constructor(private httpClient: HttpClient) { }

  getAllPayments(): Observable<PaymentCustomer[]>{
    return this.httpClient.get<PaymentCustomer[]>(this.apiUrl);
  }

  getCustomerSelectedPayment(customerID: number): Observable<PaymentCustomer[]>{
    let params = new HttpParams().set('customerId', customerID.toString())
    return this.httpClient.get<PaymentCustomer[]>(`${this.apiUrl}/pagos/abonos`, {params});
  }
}
