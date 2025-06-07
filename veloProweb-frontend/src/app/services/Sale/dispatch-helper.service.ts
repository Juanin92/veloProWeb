import { Injectable } from '@angular/core';
import { SaleDetailResponse } from '../../models/entity/sale/sale-detail-response';
import { DispatchStatus } from '../../models/enum/dispatch-status';
import { Dispatch } from '../../models/entity/sale/dispatch';
import { DispatchRequest } from '../../models/entity/sale/dispatch-request';

@Injectable({
  providedIn: 'root'
})
export class DispatchHelperService {

  constructor() { }

  getStatusLabel(status: string): string {
    return DispatchStatus[status as keyof typeof DispatchStatus] ?? status;
  }

  initializeDispatch(): Dispatch{
    return {
      id: 0,
      trackingNumber: '',
      status: '',
      address: '',
      comment: '',
      customer: '',
      hasSale: false,
      created: '',
      deliveryDate: '',
      saleDetails: [],
    }
  }

  initializeDispatchRequest(): DispatchRequest{
    return {
      address: '',
      comment: '',
      customer: '',
      saleDetails: [],
    }
  }

  initializeSaleRequest(): SaleDetailResponse{
    return {
      descriptionProduct: '',
      quantity: 0,
      price: 0,
      tax: 0,
      hasDispatch: true,
    }
  }
}
