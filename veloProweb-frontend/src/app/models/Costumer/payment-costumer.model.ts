import { Costumer } from "./costumer.model";
import { TicketHistory } from "./ticket-history.model";

export interface PaymentCostumer {
    id: number;
    amount: number;
    comment: string;
    date: string;
    costumer: Costumer;
    document: TicketHistory;
}
