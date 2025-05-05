import { Injectable } from '@angular/core';
import { Customer } from '../../models/Entity/Customer/customer.model';
import { PaymentStatus } from '../../models/enum/payment-status.enum';
import { CustomerDTO } from '../../models/Entity/Customer/dto/customer-dto';

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
    };
  }

  createEmptyCustomerDTO(): CustomerDTO {
    return {
      id: 0,
      name: '',
      surname: '',
      phone: '+569 ',
      email: ''
    }
  }

  mapCustomerToDto(customer: Customer): CustomerDTO {
    return {
      id: customer.id,
      name: customer.name,
      surname: customer.surname,
      phone: customer.phone,
      email: customer.email
    }
  }
}
