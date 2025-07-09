import { PurchaseDetailRequest } from "./purchase-detail-request";

export interface PurchaseRequest {
  supplier: string;
  documentType: string;
  document: string;
  date: string;
  tax: number;
  total: number;
  detailList: PurchaseDetailRequest[];
}
