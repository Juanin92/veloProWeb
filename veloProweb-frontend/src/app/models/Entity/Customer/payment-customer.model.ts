import { Customer } from "./customer.model";
import { TicketHistory } from "./ticket-history.model";

export interface PaymentCustomer {
    id: number;
    amount: number;
    comment: string;
    date: string;
    customer: Customer;
    document: TicketHistory;
}
