import { Injectable } from '@angular/core';
import { CustomerResponse } from '../../models/Entity/Customer/customer-response';
import { PaymentStatus } from '../../models/enum/payment-status.enum';
import { CustomerForm } from '../../models/Entity/Customer/customer-form';

@Injectable({
  providedIn: 'root',
})
export class CustomerHelperServiceService {
  /**
   * Crea un cliente con valores predeterminados
   * Sirve para resetear los valores de objeto o evitar errores por ser null
   * @returns Cliente con valores predeterminados
   */
  createEmptyCustomer(): CustomerResponse {
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
    };
  }

  createEmptyCustomerDTO(): CustomerForm {
    return {
      id: 0,
      name: '',
      surname: '',
      phone: '+569 ',
      email: '',
    };
  }

  mapCustomerToDto(customer: CustomerResponse): CustomerForm {
    return {
      id: customer.id,
      name: customer.name,
      surname: customer.surname,
      phone: customer.phone,
      email: customer.email,
    };
  }
}
