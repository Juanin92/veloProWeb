import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PaymentCustomerResponse } from '../../models/entity/customer/payment-customer-response';
import { PaymentDetails } from '../../models/entity/customer/payment-details';
import { AuthService } from '../user/auth.service';

@Injectable({
  providedIn: 'root',
})
export class PaymentCustomerService {
  private apiUrl = 'http://localhost:8080/pagos';

  constructor(private httpClient: HttpClient, private auth: AuthService) {}

  /**
   * Obtiene todos los pagos realizados
   * @returns Observable emite una lista de pagos
   */
  getAllPayments(): Observable<PaymentCustomerResponse[]> {
    return this.httpClient.get<PaymentCustomerResponse[]>(this.apiUrl);
  }

  /**
   * Obtienes lista de pagos de un client especifico
   * @param customerID ID del cliente por consultar
   * @returns Observable emite una lista con los pagos del cliente
   */
  getCustomerSelectedPayment(
    customerID: number
  ): Observable<PaymentCustomerResponse[]> {
    let params = new HttpParams().set('customerId', customerID.toString());
    return this.httpClient.get<PaymentCustomerResponse[]>(
      `${this.apiUrl}/abonos`,
      { params }
    );
  }

  /**
   * Crea un pago de cliente haciendo una petición POST
   * @param dto - Objeto con los valores para la acción
   * @returns - Observable emite un mensaje de confirmación o error
   */
  createPaymentCustomer(dto: PaymentDetails): Observable<{ message: string }> {
    return this.httpClient.post<{ message: string }>(`${this.apiUrl}`, dto, {
      headers: this.auth.getAuthHeaders(),
    });
  }
}
