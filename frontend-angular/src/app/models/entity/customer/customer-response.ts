import { PaymentStatus } from "../../enum/payment-status.enum";

export interface CustomerResponse {
    id: number;
    name: string;
    surname: string;
    phone: string;
    email: string;
    debt: number;
    totalDebt: number;
    status: PaymentStatus;
    account: boolean;
}