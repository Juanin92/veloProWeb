import { PaymentMethod } from "../enum/payment-method";
import { SaleDetailDTO } from "./sale-detail-dto";

export interface SaleRequestDTO {
    id: number;
    date: string;
    idCustomer: number | null;
    paymentMethod: PaymentMethod;
    tax: number;
    total: number;
    discount: number;
    numberDocument: number;
    comment: string;
    detailList: SaleDetailDTO[];
}
