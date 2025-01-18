import { Injectable } from '@angular/core';
import { Sale } from '../../models/Entity/Sale/sale';
import { PaymentMethod } from '../../models/enum/payment-method';

@Injectable({
  providedIn: 'root'
})
export class SaleHelperService {

  /**
     * Crea una venta con valores predeterminados
     * @returns - Venta con valores predeterminados
     */
    createEmptySale(): Sale {
      return {
        id: 0,
        date: '',
        paymentMethod: null,
        document: '',
        comment: '',
        discount: 0,
        tax: 0,
        totalSale: 0,
        status: false,
        customer: null
      }
    }
}
