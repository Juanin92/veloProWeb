import { PaymentMethod } from "../../enum/payment-method";
import { Customer } from "../Customer/customer.model";

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
    customer: Customer | null;
}
