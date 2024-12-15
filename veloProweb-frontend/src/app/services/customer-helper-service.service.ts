import { Injectable } from '@angular/core';
import { Customer } from '../models/Customer/customer.model';
import { PaymentStatus } from '../models/enum/payment-status.enum';

@Injectable({
  providedIn: 'root'
})
export class CustomerHelperServiceService {

  /**
   * Crea un cliente con valores predeterminados
   * Sirve para resetear los valores de objeto o evitar errores por ser null
   * @returns Cliente con valores predeterminados
   */
  createEmptyCustomer(): Customer {
    return {
      id: 0,
      name: '',
      surname: '',
      phone: '+569 ',
      email: '',
      debt: 0,
      totalDebt: 0,
      status: PaymentStatus.NULO,
      account: true,
      paymentCustomerList: [],
      ticketHistoryList: []
    };
  }
}
