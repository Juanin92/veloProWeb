import { PaymentMethod } from "../../enum/payment-method";
import { CustomerResponse } from "../Customer/customer-response";

export interface Sale {
    id: number;
    date: string;
    paymentMethod: PaymentMethod | null;
    document: string;
    comment: string;
    discount: number;
    tax: number;
    totalSale: number;
    status: boolean;
    customer: CustomerResponse | null;
}
