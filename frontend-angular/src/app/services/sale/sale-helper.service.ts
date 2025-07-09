import { Injectable } from '@angular/core';
import { SaleRequest } from '../../models/entity/sale/sale-request';

@Injectable({
  providedIn: 'root'
})
export class SaleHelperService {

  initializeSaleRequest(): SaleRequest{
    return {
      idCustomer: 0,
      idDispatch: 0,
      paymentMethod: null,
      tax: 0,
      total: 0,
      discount: 0,
      comment: '',
      detailList: [],
    }
  }
}
