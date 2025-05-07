import { CustomerResponse } from "./customer-response";

export interface TicketHistory {
    id: number;
    amount: number;
    document: string;
    total: number;
    status: boolean;
    date: string;
    notificationsDate: string;
    customer: CustomerResponse;
}
