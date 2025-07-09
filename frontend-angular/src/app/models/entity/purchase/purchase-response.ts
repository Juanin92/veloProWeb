import { PurchaseDetailResponse } from "./purchase-detail-response";

export interface PurchaseResponse {
    document: string,
    documentType: string,
    date: string,
    iva: number,
    purchaseTotal: number,
    supplier: string,
    detailsList: PurchaseDetailResponse[],
}
