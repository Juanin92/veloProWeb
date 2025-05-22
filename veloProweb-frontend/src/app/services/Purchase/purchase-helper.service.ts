import { Injectable } from '@angular/core';
import { Purchase } from '../../models/Entity/Purchase/purchase';
import { PurchaseRequest } from '../../models/Entity/Purchase/purchase-request';

@Injectable({
  providedIn: 'root'
})
export class PurchaseHelperService {

  /**
   * Crea una Compra con valores predeterminados
   * @returns - Compra con valores predeterminados
   */
  initializePurchase(): Purchase {
    return {
      document: '',
      documentType: 'Factura',
      tax: 0,
      total: 0,
      date: '',
      supplier: null
    }
  }

  initializePurchaseRequest(): PurchaseRequest{
    return {
      supplier: '',
      documentType: '',
      document: '',
      date: '',
      tax: 0,
      total: 0,
      detailList: []
    }
  }
}
