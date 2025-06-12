import { Injectable } from '@angular/core';
import { Sale } from '../../models/entity/sale/sale';
import { SaleDetail } from '../../models/entity/sale/sale-detail';
import { SaleRequestDTO } from '../../models/DTO/sale-request-dto';
import { SaleDetailDTO } from '../../models/DTO/sale-detail-dto';
import { PaymentMethod } from '../../models/enum/payment-method';
import { SaleRequest } from '../../models/entity/sale/sale-request';

@Injectable({
  providedIn: 'root'
})
export class SaleHelperService {

  initializeSaleRequest(): SaleRequest{
    return {
      idCustomer: 0,
      paymentMethod: null,
      tax: 0,
      total: 0,
      discount: 0,
      comment: '',
      detailList: [],
    }
  }
}
