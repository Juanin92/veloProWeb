import { PaymentMethod } from "../../enum/payment-method"
import { SaleDetailRequest } from "./sale-detail-request";

export interface SaleRequest {
    idCustomer: number,
    paymentMethod: PaymentMethod | null,
    tax: number,
    total: number,
    discount: number,
    comment: string,
    detailList: SaleDetailRequest[],
}
