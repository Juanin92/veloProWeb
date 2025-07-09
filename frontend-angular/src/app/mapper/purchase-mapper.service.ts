import { Injectable } from '@angular/core';
import { Purchase } from '../models/entity/purchase/purchase';
import { PurchaseRequest } from '../models/entity/purchase/purchase-request';
import { PurchaseDetailRequest } from '../models/entity/purchase/purchase-detail-request';
import { PurchaseDetails } from '../models/entity/purchase/purchase-details';

@Injectable({
  providedIn: 'root'
})
export class PurchaseMapperService {

  constructor() { }

  mapToPurchaseRequest(purchase: Purchase, details: PurchaseDetailRequest[]): PurchaseRequest{
    return {
      supplier: purchase.supplier!.rut,
      documentType: purchase.documentType,
      document: purchase.document,
      date: purchase.date,
      tax: purchase.tax,
      total: purchase.total,
      detailList: details,
    }
  }

  mapToPurchaseDetailRequest(details: PurchaseDetails[]): PurchaseDetailRequest[]{
    return details.map(detail => this.mapDetailToRequest(detail));
  }

  mapDetailToRequest(detail: PurchaseDetails): PurchaseDetailRequest{
    return  {
      idProduct: detail.product.id,
      idPurchase: 0,
      price: detail.price,
      tax: detail.tax,
      quantity: detail.quantity,
      total: detail.total
    }
  }
}
