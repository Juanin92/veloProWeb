import { Injectable } from '@angular/core';
import { PurchaseDetails } from '../../models/Entity/Purchase/purchase-details';
import { Purchase } from '../../models/Entity/Purchase/purchase';
import { PurchaseRequestDTO } from '../../models/DTO/purchase-request-dto';
import { PurchaseDetailDTO } from '../../models/DTO/purchase-detail-dto';

@Injectable({
  providedIn: 'root'
})
export class PurchaseHelperService {

  /**
   * Crea una Compra con valores predeterminados
   * @returns - Compra con valores predeterminados
   */
  createEmptyPurchase(): Purchase {
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

  /**
   * Crea un DTO de compras para unir la información de una compra y sus detalles, 
   * @param purchase - Objeto compra con los valores necesarios
   * @param details - lista de detalle de compra con valores
   * @returns - DTO con los valores necesarios para realizar una solicitud.
   */
  createDto(purchase: Purchase, details: PurchaseDetails[]): PurchaseRequestDTO {
    return {
      id: 0,
      date: purchase.date,
      idSupplier: 0,
      documentType: purchase.documentType,
      document: purchase.document,
      tax: purchase.tax,
      total: purchase.purchaseTotal,
      detailList: details.map(detail => this.createDetailDto(detail)),
    };
  }

  /**
   * Crea un DTO de detalle de compras
   * @param detail - Objeto detalle de compra a utilizar sus valores
   * @returns - DTO con valores necesarios
   */
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
