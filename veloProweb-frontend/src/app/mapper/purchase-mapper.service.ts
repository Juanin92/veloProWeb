import { Injectable } from '@angular/core';
import { Purchase } from '../models/Entity/Purchase/purchase';
import { PurchaseRequest } from '../models/Entity/Purchase/purchase-request';

@Injectable({
  providedIn: 'root'
})
export class PurchaseMapperService {

  constructor() { }

  mapToPurchaseRequest(purchase: Purchase): PurchaseRequest{
    return {
      supplier: purchase.supplier!.rut,
      documentType: purchase.documentType,
      document: purchase.document,
      date: purchase.date,
      tax: purchase.tax,
      total: purchase.total,
      detailList: []
    }
  }
}
