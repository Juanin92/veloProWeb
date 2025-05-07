import { CustomerResponse } from "./customer-response";
import { TicketHistory } from "./ticket-history.model";

export interface PaymentCustomer {
    id: number;
    amount: number;
    comment: string;
    date: string;
    customer: CustomerResponse;
    document: TicketHistory;
}
