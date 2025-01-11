import { Injectable } from '@angular/core';
import { PurchaseDetails } from '../../models/Entity/Purchase/purchase-details';
import { Purchase } from '../../models/Entity/Purchase/purchase';
import { PurchaseRequestDTO } from '../../models/DTO/purchase-request-dto';
import { PurchaseDetailDTO } from '../../models/DTO/purchase-detail-dto';

@Injectable({
  providedIn: 'root'
})
export class PurchaseHelperService {

  createEmptyPurchase(): Purchase{
    return { 
      id: 0,
      document: '',
      documentType: 'Factura',
      tax: 0,
      purchaseTotal: 0,
      date: '',
      supplier: null
    }
  }

  createDto(purchase: Purchase, details: PurchaseDetails[]): PurchaseRequestDTO {
    return {
      id: 0,
      date: purchase.date,
      idSupplier: purchase.supplier?.id ?? 0,
      documentType: purchase.documentType,
      document: purchase.document,
      tax: purchase.tax,
      total: purchase.purchaseTotal,
      detailList: details.map(detail => this.createDetailDto(detail)),
    };
  }

  private createDetailDto(detail: PurchaseDetails): PurchaseDetailDTO {
    return {
      id: detail.id,
      quantity: detail.quantity,
      price: detail.price,
      tax: detail.tax,
      total: detail.total,
      idPurchase: 0,
      idProduct: detail.product.id,
    };
  }
}
