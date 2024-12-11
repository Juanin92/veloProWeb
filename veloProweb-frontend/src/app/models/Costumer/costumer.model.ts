import { PaymentStatus } from "../enum/payment-status.enum";
import { PaymentCostumer } from "./payment-costumer.model";
import { TicketHistory } from "./ticket-history.model";

export interface Costumer {
    id: number;
    name: string;
    surname: string;
    phone: string;
    email: string;
    debt: number;
    totalDebt: number;
    status: PaymentStatus;
    account: boolean;
    paymentCostumerList: PaymentCostumer[];
    ticketHistoryList: TicketHistory[];
}