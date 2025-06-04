import { Injectable } from '@angular/core';
import { Sale } from '../../models/entity/sale/sale';
import { SaleDetail } from '../../models/entity/sale/sale-detail';
import { SaleRequestDTO } from '../../models/DTO/sale-request-dto';
import { SaleDetailDTO } from '../../models/DTO/sale-detail-dto';

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

  /**
     * Crea un DTO de ventas para unir la información de una venta y sus detalles, 
     * @param sale - Objeto venta con los valores necesarios
     * @param details - lista de detalle de venta con valores
     * @returns - DTO con los valores necesarios para realizar una solicitud.
     */
  createDto(sale: Sale, details: SaleDetail[], numberDocument: number, totalSale: number,
    discount: number): SaleRequestDTO {
    return {
      id: 0,
      date: sale.date,
      idCustomer: sale.customer?.id ?? null,
      paymentMethod: sale.paymentMethod!,
      tax: sale.tax,
      total: totalSale,
      discount: discount,
      numberDocument: numberDocument,
      comment: sale.comment,
      detailList: details.map(detail => this.createDetailDto(detail)),
    };
  }

  /**
   * Crea un DTO de detalle de ventas
   * @param detail - Objeto detalle de ventas a utilizar sus valores
   * @returns - DTO con valores necesarios
   */
  private createDetailDto(detail: SaleDetail): SaleDetailDTO {
    return {
      id: detail.id,
      idProduct: detail.product.id,
      quantity: detail.quantity,
    };
  }
}
