import { Injectable } from '@angular/core';
import { SaleDetail } from '../models/entity/sale/sale-detail';
import { SaleDetailRequest } from '../models/entity/sale/sale-detail-request';
import { SaleDetailResponse } from '../models/entity/sale/sale-detail-response';

@Injectable({
  providedIn: 'root'
})
export class SaleMapperService {

  constructor() { }

  mapToSaleDetailRequest(details: SaleDetail[]): SaleDetailRequest[]{
      return details.map(detail => this.mapDetailToRequest(detail));
  }

  mapSaleDetailResponseToRequest(details: SaleDetailResponse[]): SaleDetailRequest[]{
      return details.map(detail => this.mapDetailResponseToRequest(detail));
  }

  private mapDetailToRequest(detail: SaleDetail): SaleDetailRequest{
    return {
      idProduct: detail.product.id,
      quantity: detail.quantity,
    }
  }

  private mapDetailResponseToRequest(detail: SaleDetailResponse): SaleDetailRequest{
    return {
      idProduct: detail.idProduct,
      quantity: detail.quantity,
    }
  }
}
