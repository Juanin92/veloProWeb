import { PaymentStatus } from "../enum/payment-status.enum";
import { PaymentCustomer } from "./payment-customer.model";
import { TicketHistory } from "./ticket-history.model";

export interface Customer {
    id: number;
    name: string;
    surname: string;
    phone: string;
    email: string;
    debt: number;
    totalDebt: number;
    status: PaymentStatus;
    account: boolean;
    paymentCustomerList: PaymentCustomer[];
    ticketHistoryList: TicketHistory[];
}