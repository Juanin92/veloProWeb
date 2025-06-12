import { PaymentMethod } from "../../enum/payment-method";
import { SaleDetailResponse } from "./sale-detail-response";

export interface Sale {
    date: string,
    paymentMethod: PaymentMethod,
    document: string,
    comment: string,
    discount: number,
    tax: number,
    totalSale: number,
    status: boolean,
    customer: string,
    notification: string,
    ticketStatus: boolean,
    saleDetails: SaleDetailResponse[],
}
