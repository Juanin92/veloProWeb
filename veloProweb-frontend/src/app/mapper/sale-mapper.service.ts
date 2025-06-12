import { Injectable } from '@angular/core';
import { SaleDetail } from '../models/entity/sale/sale-detail';
import { SaleDetailRequest } from '../models/entity/sale/sale-detail-request';

@Injectable({
  providedIn: 'root'
})
export class SaleMapperService {

  constructor() { }

  mapToSaleDetailRequest(details: SaleDetail[]): SaleDetailRequest[]{
      return details.map(detail => this.mapDetailToRequest(detail));
  }

  mapDetailToRequest(detail: SaleDetail): SaleDetailRequest{
    return {
      idProduct: detail.product.id,
      quantity: detail.quantity,
    }
  }
}
