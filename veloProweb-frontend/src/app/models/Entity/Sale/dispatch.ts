import { SaleDetailResponse } from "./sale-detail-response";

export interface Dispatch {
    id: number,
    trackingNumber: string,
    status: string,
    address: string,
    comment: string,
    customer: string,
    hasSale: boolean,
    created: string,
    deliveryDate: string,
    saleDetails: SaleDetailResponse[]
}
